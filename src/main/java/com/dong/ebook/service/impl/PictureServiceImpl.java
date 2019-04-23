package com.dong.ebook.service.impl;

import com.alibaba.fastjson.JSON;
import com.dong.ebook.common.PreferenceTypeName;
import com.dong.ebook.common.UserRole;
import com.dong.ebook.dao.PictureDao;
import com.dong.ebook.dto.*;
import com.dong.ebook.model.*;
import com.dong.ebook.security.AuthUserService;
import com.dong.ebook.service.ElasticsearchService;
import com.dong.ebook.service.PictureService;
import com.dong.ebook.service.PreferenceService;
import com.dong.ebook.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PictureServiceImpl implements PictureService {
    private static Logger logger = Logger.getLogger(PictureServiceImpl.class);

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @Autowired
    PictureDao pictureDao;

    @Autowired
    AuthUserService authUserService;

    @Autowired
    UserService userService;

    @Autowired
    PreferenceService preferenceService;

    @Autowired
    ElasticsearchService elasticsearchService;

    @Override
    public ResponseCommonDto savePicture(RequestPictureDto requestPictureDto) {
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(false);

        if(authUserService.getCurUser().getIslock()){
            responseCommonDto.setErrorMsg("你已被上锁，不能上传任何信息");
            return responseCommonDto;
        }

        if(requestPictureDto.getTitle() == null || requestPictureDto.getTitle().isEmpty()){
            responseCommonDto.setErrorMsg("标题为空");
            return responseCommonDto;
        }

        //用id判断是上传还是修改
        Long id = requestPictureDto.getId();
        String requestUrlJson = requestPictureDto.getUrlJson();
        if(id == null){
            if(requestUrlJson == null || requestUrlJson.isEmpty()){
                responseCommonDto.setErrorMsg("savePicture, url is empty");
                return responseCommonDto;
            }
            List<Picture> pictures = RequestPictureDto2dos(requestPictureDto);
            // ** 这里可以优化成批量插入 **
            for(Picture picture : pictures){
                insertPicture(picture);
            }
        } else if(id > 0){
            //修改picture
            updatePicture(requestPictureDto);
        } else {
            responseCommonDto.setErrorMsg("id error");
            return responseCommonDto;
        }

        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    @Override
    public ResponseManagerPictureListDto getManagerPictureList(int pageNum, int pageSize, boolean desc) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        PictureExample pictureExample = new PictureExample();
        if(desc){
            pictureExample.setOrderByClause("modify_time desc");
        } else {
            pictureExample.setOrderByClause("modify_time asc");
        }
        pictureDao.selectByExample(pictureExample);
        PageInfo pageInfo = new PageInfo(page.getResult());
        return assembleResponseManagerPictureListDto(pageInfo);
    }

    @Override
    public ResponseManagerPictureListDto getManagerPictureList(int pageNum, int pageSize, boolean desc, String query) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        PictureExample pictureExample = assemblePictureExampleByDesc(desc);
        if(query != null && !query.isEmpty()){
            pictureExample.createCriteria().andTitleLike(query);
        }
        pictureDao.selectByExample(pictureExample);
        PageInfo pageInfo = new PageInfo(page.getResult());
        return assembleResponseManagerPictureListDto(pageInfo);
    }

    @Override
    public ResponsePictureDto getPicture(long id) {
        ResponsePictureDto responsePictureDto = new ResponsePictureDto();
        responsePictureDto.setSuccess(false);
        if(id < 1){
            responsePictureDto.setErrorMsg("id error");
            return responsePictureDto;
        }
        Picture picture = pictureDao.selectByPrimaryKey(id);
        PictureDto pictureDto = dozerBeanMapper.map(picture, PictureDto.class);
        responsePictureDto.setPictureDto(pictureDto);
        responsePictureDto.setSuccess(true);
        return responsePictureDto;
    }

    @Override
    public ResponseCommonDto delPicture(Long id) {
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(false);
        if(id < 1){
            logger.warn("delPicture id = " + id);
            responseCommonDto.setErrorMsg("id error");
            return responseCommonDto;
        }
        User curUser = authUserService.getCurUser();
        if (!UserRole.SUPERADMIN.equals(curUser.getRole())) {
            responseCommonDto.setErrorMsg("只有超级管理员可以删除音乐");
            return responseCommonDto;
        }
        pictureDao.deleteByPrimaryKey(id);
        elasticsearchService.delPicture(id);
        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    @Override
    public ResponseMainPagePictureListDto getMainPagePictureList() {
        int bigSize = 1, smallSize = 6, circleSize = 6;
        int totalSize = bigSize + smallSize + circleSize;
        List<Picture> pictures = getPictureListByPreference(totalSize);
        return assembleResponseMainPagePictureListDto(pictures, bigSize, smallSize, circleSize);
    }

    @Override
    public ResponseMobileMainPagePictureListDto getMobileMainPagePictureList() {
        int size = 8;
        List<Picture> pictures = getPictureListByPreference(size);
        List<PictureDto> pictureDtos = pictures2Dto(pictures);
        ResponseMobileMainPagePictureListDto responseMobileMainPagePictureListDto = new ResponseMobileMainPagePictureListDto();
        responseMobileMainPagePictureListDto.setPictures(pictureDtos);
        responseMobileMainPagePictureListDto.setSuccess(true);
        return responseMobileMainPagePictureListDto;
    }

    public List<Picture> getPictureListByPreference(int size){
        List<Picture> pictures;
        User user = authUserService.getCurUser();
        if(user == null){
            pictures = getPictureList(1, size, true);
        }else{
            //根据兴趣爱好找
            List<Long> typeIdList = preferenceService.getPreferenceTypeId(user.getId(), PreferenceTypeName.PICTURE);
            pictures = getPictureListByTypeId(1, size, true, typeIdList);
            if(pictures.size() < size){
                //数量不够就找别的
                pictures.addAll(getPictureListByNotTypeId(1, size - pictures.size(), true, typeIdList));
            }
        }
        if(pictures.size() > size){
            logger.info("getMainPageVideoList pictures.size()=" + pictures.size() + " > size=" + size);
            pictures = pictures.subList(0, size);
        }
        return pictures;
    }

    private ResponseMainPagePictureListDto assembleResponseMainPagePictureListDto(List<Picture> pictures, int bigSize, int smallSize, int circleSize) {
        List<PictureDto> bigPictureDtos = new ArrayList<>(bigSize);
        List<PictureDto> smallPictureDtos = new ArrayList<>(smallSize);
        List<PictureDto> circlePictureDtos = new ArrayList<>(circleSize);

        int idx = 0;
        for(Picture picture : pictures){
            //删除不需要的数据
            picture.setCreateTime(null);
            picture.setModifyTime(null);
            picture.setPictureTypeId(null);
            picture.setModifyUserId(null);

            PictureDto pictureDto = do2dtos(picture);
            if(++idx <= bigSize){
                bigPictureDtos.add(pictureDto);
            }else if(idx <= bigSize + smallSize){
                smallPictureDtos.add(pictureDto);
            }else if(idx <= bigSize + smallSize + circleSize){
                circlePictureDtos.add(pictureDto);
            }
        }
        ResponseMainPagePictureListDto responseMainPagePictureListDto = new ResponseMainPagePictureListDto();
        responseMainPagePictureListDto.setSuccess(true);
        responseMainPagePictureListDto.setBigPictureDtos(bigPictureDtos);
        responseMainPagePictureListDto.setSmallPictureDtos(smallPictureDtos);
        responseMainPagePictureListDto.setCirclePictureDtos(circlePictureDtos);
        return responseMainPagePictureListDto;
    }

    public Picture getPicture(String title){
        PictureExample pictureExample = new PictureExample();
        PictureExample.Criteria criteria = pictureExample.createCriteria();
        criteria.andTitleEqualTo(title);
        List<Picture> pictures = pictureDao.selectByExample(pictureExample);
        if(pictures.size() > 0){
            return pictures.get(0);
        }
        return null;
    }

    public List<Picture> getPictureList(int pageNum, int pageSize, boolean desc){
        Page<Picture> page = PageHelper.startPage(pageNum, pageSize);
        PictureExample pictureExample = assemblePictureExampleByDesc(desc);
        pictureDao.selectByExample(pictureExample);
        return page.getResult();
    }

    public Picture getPictureById(long id){
        if(id < 1){
            return null;
        }
        return pictureDao.selectByPrimaryKey(id);
    }

    public void updatePicture(Picture picture){
        picture.setModifyTime(new Date());
        picture.setModifyUserId(authUserService.getCurUser().getId());
        pictureDao.updateByPrimaryKeySelective(picture);
        elasticsearchService.updatePicture(Picture2Elasticsearch(picture));
    }

    public void updatePicture(RequestPictureDto requestPictureDto){
        Picture picture = RequestPictureDto2do(requestPictureDto);
        picture.setModifyTime(new Date());
        picture.setModifyUserId(authUserService.getCurUser().getId());
        pictureDao.updateByPrimaryKeySelective(picture);
        elasticsearchService.updatePicture(Picture2Elasticsearch(picture));
    }

    public void insertPicture(Picture picture){
        Date date = new Date();
        picture.setTraffic(0);
        picture.setCreateTime(date);
        picture.setModifyTime(date);
        picture.setModifyUserId(authUserService.getCurUser().getId());
        pictureDao.insert(picture);
        elasticsearchService.addPicture(Picture2Elasticsearch(picture));
    }

    public Picture RequestPictureDto2do(RequestPictureDto requestPictureDto){
        Picture picture = new Picture();
        picture.setId(requestPictureDto.getId());
        picture.setFileUrl(requestPictureDto.getUrlJson());
        picture.setTitle(requestPictureDto.getTitle());
        picture.setPictureTypeId(requestPictureDto.getPictureTypeId());
        return picture;
    }

    public List<Picture> RequestPictureDto2dos(RequestPictureDto requestPictureDto){
        List<String> fileUrls = JSON.parseArray(requestPictureDto.getUrlJson(), String.class);
        List<Picture> pictures = new ArrayList<>(fileUrls.size());
        for(String url : fileUrls){
            Picture picture = dozerBeanMapper.map(requestPictureDto, Picture.class);
            picture.setFileUrl(url);
            pictures.add(picture);
        }
        return pictures;
    }

    private ResponseManagerPictureListDto assembleResponseManagerPictureListDto(PageInfo pageInfo) {
        List<Picture> pictures = pageInfo.getList();
        List<ManagerPictureDto> managerPictureDtos = new ArrayList<>(pictures.size());
        for(Picture picture : pictures){
            ManagerPictureDto managerPictureDto = dozerBeanMapper.map(picture, ManagerPictureDto.class);
            managerPictureDtos.add(managerPictureDto);

            String userNickname = userService.findUserById(picture.getModifyUserId()).getNickname();
            managerPictureDto.setModifyUserNickname(userNickname);
        }
        pageInfo.setList(managerPictureDtos);

        ResponseManagerPictureListDto responseManagerPictureListDto = new ResponseManagerPictureListDto();
        responseManagerPictureListDto.setPageInfo(pageInfo);
        responseManagerPictureListDto.setSuccess(true);
        return responseManagerPictureListDto;
    }

    public PictureExample assemblePictureExampleByDesc(boolean desc){
        PictureExample pictureExample = new PictureExample();
        if(desc){
            pictureExample.setOrderByClause("modify_time desc");
        } else {
            pictureExample.setOrderByClause("modify_time asc");
        }
        return pictureExample;
    }

    private PictureDto do2dtos(Picture picture) {
        return dozerBeanMapper.map(picture, PictureDto.class);
    }

    public ElasticsearchPictureDto Picture2Elasticsearch(Picture picture){
        return dozerBeanMapper.map(picture, ElasticsearchPictureDto.class);
    }

    public PictureDto picture2Dto(Picture picture){
        return dozerBeanMapper.map(picture, PictureDto.class);
    }

    public List<PictureDto> pictures2Dto(List<Picture> pictures){
        List<PictureDto> pictureDtos = new ArrayList<>(pictures.size());
        for(Picture picture : pictures){
            pictureDtos.add(picture2Dto(picture));
        }
        return pictureDtos;
    }

    public List<Picture> getPictureListByTypeId(int pageNum, int pageSize, boolean desc, List<Long> typeIds) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        PictureExample pictureExample = assemblePictureExampleByDesc(desc);
        if(typeIds.size() > 0){
            pictureExample.createCriteria().andPictureTypeIdIn(typeIds);
        }
        pictureDao.selectByExample(pictureExample);
        return page.getResult();
    }

    public List<Picture> getPictureListByNotTypeId(int pageNum, int pageSize, boolean desc, List<Long> typeIds) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        PictureExample pictureExample = assemblePictureExampleByDesc(desc);
        if(typeIds.size() > 0){
            pictureExample.createCriteria().andPictureTypeIdNotIn(typeIds);
        }
        pictureDao.selectByExample(pictureExample);
        return page.getResult();
    }
}

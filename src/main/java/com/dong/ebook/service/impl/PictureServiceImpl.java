package com.dong.ebook.service.impl;

import com.alibaba.fastjson.JSON;
import com.dong.ebook.common.UserRole;
import com.dong.ebook.dao.PictureDao;
import com.dong.ebook.dto.*;
import com.dong.ebook.model.Picture;
import com.dong.ebook.model.PictureExample;
import com.dong.ebook.model.User;
import com.dong.ebook.security.AuthUserService;
import com.dong.ebook.service.PictureService;
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
    private PictureDao pictureDao;

    @Autowired
    AuthUserService authUserService;

    @Autowired
    UserService userService;

    @Override
    public ResponseCommonDto savePicture(RequestPictureDto requestPictureDto) {
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(false);
        if(requestPictureDto.getTitle() == null || requestPictureDto.getTitle().isEmpty()){
            responseCommonDto.setErrorMsg("标题为空");
            return responseCommonDto;
        }
        String requestUrlJson = requestPictureDto.getUrlJson();
        List<String> requestUrlList = requestPictureDto.getUrls();
        if(requestUrlList == null && requestUrlJson == null){
            responseCommonDto.setErrorMsg("url为空");
            return responseCommonDto;
        }

        //用id判断是上传还是修改
        Long id = requestPictureDto.getId();
        Picture picture = getPicture(requestPictureDto.getTitle());
        if(id == null){
            //上传picture
            if(picture != null){
                //追加url
                addUrls(picture, requestUrlList);
            } else{
                picture = RequestPictureDto2do(requestPictureDto);
                insertPicture(picture);
            }
        } else if(id > 0){
            //修改picture
            if(requestUrlJson == null){
                responseCommonDto.setErrorMsg("update picture, urlJson is empty");
                return responseCommonDto;
            }
            //检查标题是否有重复
            if(picture != null){
                if(id.equals(picture.getId())){
                    //说明没有修改title
                    picture.setUrlJson(requestUrlJson);
                    updatePicture(picture);
                } else{
                    //修改了标题并且和别的标题重复
                    //删除修改的这行记录
                    pictureDao.deleteByPrimaryKey(id);
                    //追加url
                    addUrls(picture, requestUrlJson);
                }
            } else{
                //说明修改了标题，并且没有重复的标题
                updatePicture(requestPictureDto);
            }
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
        pictureDao.selectByExampleWithBLOBs(pictureExample);
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
        pictureDao.selectByExampleWithBLOBs(pictureExample);
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
        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    public Picture getPicture(String title){
        PictureExample pictureExample = new PictureExample();
        PictureExample.Criteria criteria = pictureExample.createCriteria();
        criteria.andTitleEqualTo(title);
        List<Picture> pictures = pictureDao.selectByExampleWithBLOBs(pictureExample);
        if(pictures.size() > 0){
            return pictures.get(0);
        }
        return null;
    }

    public Picture getPictureById(long id){
        if(id < 1){
            return null;
        }
        return pictureDao.selectByPrimaryKey(id);
    }

    public void addUrls(Picture picture, List<String> urls){
        List<String> curUrls = JSON.parseArray(picture.getUrlJson(), String.class);
        if(curUrls == null){
            curUrls = urls;
        }else{
            curUrls.addAll(urls);
        }
        String urlsStr = JSON.toJSONString(curUrls);
        picture.setUrlJson(urlsStr);
        picture.setModifyTime(new Date());
        picture.setModifyUserId(authUserService.getCurUser().getId());
        pictureDao.updateByPrimaryKeySelective(picture);
    }

    public void addUrls(Picture picture, String urlJson){
        List<String> newUrls = JSON.parseArray(urlJson, String.class);
        addUrls(picture, newUrls);
    }

    public void updatePicture(Picture picture){
        picture.setModifyTime(new Date());
        picture.setModifyUserId(authUserService.getCurUser().getId());
        pictureDao.updateByPrimaryKeySelective(picture);
    }

    public void updatePicture(RequestPictureDto requestPictureDto){
        Picture picture = new Picture();
        picture.setId(requestPictureDto.getId());
        picture.setTitle(requestPictureDto.getTitle());
        if(requestPictureDto.getUrlJson() == null){
            String urlsStr = JSON.toJSONString(requestPictureDto.getUrls());
            picture.setUrlJson(urlsStr);
        } else{
            picture.setUrlJson(requestPictureDto.getUrlJson());
        }
        picture.setModifyTime(new Date());
        picture.setModifyUserId(authUserService.getCurUser().getId());
        pictureDao.updateByPrimaryKeySelective(picture);
    }

    public void insertPicture(Picture picture){
        Date date = new Date();
        picture.setCreateTime(date);
        picture.setModifyTime(date);
        picture.setModifyUserId(authUserService.getCurUser().getId());
        pictureDao.insert(picture);
    }

    public Picture RequestPictureDto2do(RequestPictureDto requestPictureDto){
        Picture picture = dozerBeanMapper.map(requestPictureDto, Picture.class);
        //url需要从list -> jsonStr
        String urlsStr = JSON.toJSONString(requestPictureDto.getUrls());
        picture.setUrlJson(urlsStr);
        return picture;
    }

    private ResponseManagerPictureListDto assembleResponseManagerPictureListDto(PageInfo pageInfo) {
        List<Picture> pictures = pageInfo.getList();
        List<ManagerPictureDto> managerPictureDtos = new ArrayList<>();
        for(Picture picture : pictures){
            ManagerPictureDto managerPictureDto = dozerBeanMapper.map(picture, ManagerPictureDto.class);
            managerPictureDtos.add(managerPictureDto);

            String userNickname = userService.findUserById(picture.getModifyUserId()).getNickname();
            managerPictureDto.setModifyUserNickname(userNickname);

            //一个标题有很多图片，把第一张拿出来当封面
            try {
                List<String> urls = JSON.parseArray(picture.getUrlJson(), String.class);
                if(urls != null && urls.size() > 0){
                    managerPictureDto.setCoverUrl(urls.get(0));
                }
            }catch (Exception e){
                logger.error("picture id=" + picture.getId() + "存放了错误Json的Url=" + picture.getUrlJson());
            }
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
}

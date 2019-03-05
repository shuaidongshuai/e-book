package com.dong.ebook.service.impl;

import com.dong.ebook.common.PreferenceTypeName;
import com.dong.ebook.common.UserRole;
import com.dong.ebook.dao.VideoDao;
import com.dong.ebook.dto.*;
import com.dong.ebook.model.*;
import com.dong.ebook.security.AuthUserService;
import com.dong.ebook.service.ElasticsearchService;
import com.dong.ebook.service.PreferenceService;
import com.dong.ebook.service.UserService;
import com.dong.ebook.service.VideoService;
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
public class VideoServiceImpl implements VideoService {
    private static Logger logger = Logger.getLogger(VideoServiceImpl.class);

    @Autowired
    VideoDao videoDao;

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @Autowired
    AuthUserService authUserService;

    @Autowired
    UserService userService;

    @Autowired
    PreferenceService preferenceService;

    @Autowired
    ElasticsearchService elasticsearchService;

    @Override
    public ResponseCommonDto saveVideo(RequestVideoDto requestVideoDto) {
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(false);

        if(authUserService.getCurUser().getIslock()){
            responseCommonDto.setErrorMsg("你已被上锁，不能上传任何信息");
            return responseCommonDto;
        }

        Video video = RequestVideo2do(requestVideoDto);
        if(requestVideoDto.getId() == null){
            if(getVideoByFileUrl(requestVideoDto.getFileUrl()) != null){
                responseCommonDto.setErrorMsg("此视频已经创建");
                return responseCommonDto;
            }
            insertVideo(video);
        } else {
            updateVideoById(video);
        }
        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    @Override
    public ResponseCommonDto delVideo(long id) {
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(false);
        if(id < 1){
            logger.warn("delVideo id = " + id);
            responseCommonDto.setErrorMsg("id error");
            return responseCommonDto;
        }
        User curUser = authUserService.getCurUser();
        if (!UserRole.SUPERADMIN.equals(curUser.getRole())) {
            responseCommonDto.setErrorMsg("只有超级管理员可以删除视频");
            return responseCommonDto;
        }
        videoDao.deleteByPrimaryKey(id);
        elasticsearchService.delVideo(id);
        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    @Override
    public ResponseManagerVideoListDto getManagerVideoList(int pageNum, int pageSize, boolean desc) {
        return getManagerVideoList(pageNum, pageSize, desc, null);
    }

    @Override
    public ResponseManagerVideoListDto getManagerVideoList(int pageNum, int pageSize, boolean desc, String query) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        VideoExample videoExample = assembleVideoExampleByDesc(desc);
        if(query != null && !query.isEmpty()){
            videoExample.createCriteria().andTitleLike(query);
        }
        videoDao.selectByExample(videoExample);
        PageInfo pageInfo = new PageInfo(page.getResult());
        return assembleResponseManagerVideoListDto(pageInfo);
    }

    @Override
    public ResponseVideoDto getVideo(long id) {
        ResponseVideoDto responseVideoDto = new ResponseVideoDto();
        responseVideoDto.setSuccess(false);
        if(id < 1){
            responseVideoDto.setErrorMsg("id error");
            return responseVideoDto;
        }
        Video video = videoDao.selectByPrimaryKey(id);
        VideoDto videoDto = dozerBeanMapper.map(video, VideoDto.class);
        responseVideoDto.setVideoDto(videoDto);
        responseVideoDto.setSuccess(true);
        return responseVideoDto;
    }

    @Override
    public ResponseMainPageVideoListDto getMainPageVideoList() {
        //视频个数
        int size = 5;
        User user = authUserService.getCurUser();
        if(user == null){
            return getVideoList(1, size, true);
        }
        //用户登录以后需要根据兴趣推荐
        List<Long> typeIdList = preferenceService.getPreferenceTypeId(user.getId(), PreferenceTypeName.VIDEO);
        //根据兴趣爱好找
        List<Video> videos = getVideoListByTypeId(1, size, true, typeIdList);
        if(videos.size() < size){
            //数量不够就找别的
            videos.addAll(getVideoListByNotTypeId(1, size - videos.size(), true, typeIdList));
        }
        if(videos.size() > size){
            logger.info("getMainPageVideoList videos.size()=" + videos.size() + " > size=" + size);
            videos = videos.subList(0, size);
        }
        return assembleResponseMainPageVideoListDto(videos);
    }

    public ResponseMainPageVideoListDto getVideoList(int pageNum, int pageSize, boolean desc) {
        Page<Video> page = PageHelper.startPage(pageNum, pageSize);
        VideoExample videoExample = assembleVideoExampleByDesc(desc);
        videoDao.selectByExample(videoExample);
        return assembleResponseMainPageVideoListDto(page.getResult());
    }

    public Video getVideoByFileUrl(String fileUrl){
        VideoExample videoExample = new VideoExample();
        VideoExample.Criteria criteria = videoExample.createCriteria();
        criteria.andFileUrlEqualTo(fileUrl);
        List<Video> videos = videoDao.selectByExample(videoExample);
        if(videos.size() > 0){
            return videos.get(0);
        }
        return null;
    }

    public Video RequestVideo2do(RequestVideoDto requestVideoDto){
        return dozerBeanMapper.map(requestVideoDto, Video.class);
    }

    public void insertVideo(Video video){
        Date date = new Date();
        video.setTraffic(0);
        video.setCreateTime(date);
        video.setModifyTime(date);
        video.setModifyUserId(authUserService.getCurUser().getId());
        videoDao.insertSelective(video);
        elasticsearchService.addVideo(Video2Elasticsearch(video));
    }

    public void updateVideoById(Video video){
        video.setModifyTime(new Date());
        video.setModifyUserId(authUserService.getCurUser().getId());
        videoDao.updateByPrimaryKeySelective(video);
        elasticsearchService.updateVideo(Video2Elasticsearch(video));
    }

    private ResponseManagerVideoListDto assembleResponseManagerVideoListDto(PageInfo pageInfo) {
        List<Video> videos = pageInfo.getList();
        List<ManagerVideoDto> managerVideoDtos = new ArrayList<>();
        for(Video video : videos){
            ManagerVideoDto managerVideoDto = dozerBeanMapper.map(video, ManagerVideoDto.class);
            managerVideoDtos.add(managerVideoDto);

            String userNickname = userService.findUserById(video.getModifyUserId()).getNickname();
            managerVideoDto.setModifyUserNickname(userNickname);
            //裁剪title
            if(managerVideoDto.getTitle().length() > 20){
                managerVideoDto.setTitle(managerVideoDto.getTitle().substring(0, 20));
            }
        }
        pageInfo.setList(managerVideoDtos);

        ResponseManagerVideoListDto responseManagerVideoListDto = new ResponseManagerVideoListDto();
        responseManagerVideoListDto.setPageInfo(pageInfo);
        responseManagerVideoListDto.setSuccess(true);
        return responseManagerVideoListDto;
    }

    private ResponseMainPageVideoListDto assembleResponseMainPageVideoListDto(List<Video> videos) {
        List<VideoDto> videoDtos = new ArrayList<>();
        for(Video video : videos){
            //裁剪title
            if(video.getTitle().length() > 20){
                video.setTitle(video.getTitle().substring(0, 20));
            }
            //删除不需要的数据
            video.setCreateTime(null);
            video.setModifyTime(null);
            video.setVideoTypeId(null);
            video.setModifyUserId(null);
            //fileUrl也不需要
            video.setFileUrl(null);

            VideoDto videoDto = dozerBeanMapper.map(video, VideoDto.class);
            videoDtos.add(videoDto);
        }

        ResponseMainPageVideoListDto responseMainPageVideoListDto = new ResponseMainPageVideoListDto();
        responseMainPageVideoListDto.setVideoDtos(videoDtos);
        responseMainPageVideoListDto.setSuccess(true);
        return responseMainPageVideoListDto;
    }

    public VideoExample assembleVideoExampleByDesc(boolean desc){
        VideoExample videoExample = new VideoExample();
        if(desc){
            videoExample.setOrderByClause("modify_time desc");
        } else {
            videoExample.setOrderByClause("modify_time asc");
        }
        return videoExample;
    }

    private ElasticsearchVideoDto Video2Elasticsearch(Video video) {
        return dozerBeanMapper.map(video, ElasticsearchVideoDto.class);
    }

    public List<Video> getVideoListByTypeId(int pageNum, int pageSize, boolean desc, List<Long> typeIds) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        VideoExample videoExample = assembleVideoExampleByDesc(desc);
        if(typeIds.size() > 0){
            videoExample.createCriteria().andVideoTypeIdIn(typeIds);
        }
        videoDao.selectByExample(videoExample);
        return page.getResult();
    }

    public List<Video> getVideoListByNotTypeId(int pageNum, int pageSize, boolean desc, List<Long> typeIds) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        VideoExample videoExample = assembleVideoExampleByDesc(desc);
        if(typeIds.size() > 0){
            videoExample.createCriteria().andVideoTypeIdNotIn(typeIds);
        }
        videoDao.selectByExample(videoExample);
        return page.getResult();
    }
}

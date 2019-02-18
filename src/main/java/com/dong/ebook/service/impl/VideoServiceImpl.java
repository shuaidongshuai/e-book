package com.dong.ebook.service.impl;

import com.dong.ebook.common.UserRole;
import com.dong.ebook.dao.VideoDao;
import com.dong.ebook.dto.*;
import com.dong.ebook.model.User;
import com.dong.ebook.model.Video;
import com.dong.ebook.model.VideoExample;
import com.dong.ebook.security.AuthUserService;
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

    @Override
    public ResponseCommonDto saveVideo(RequestVideoDto requestVideoDto) {
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(false);

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
        video.setCreateTime(date);
        video.setModifyTime(date);
        video.setModifyUserId(authUserService.getCurUser().getId());
        videoDao.insertSelective(video);
    }

    public void updateVideoById(Video video){
        video.setModifyTime(new Date());
        video.setModifyUserId(authUserService.getCurUser().getId());
        videoDao.updateByPrimaryKeySelective(video);
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

    public VideoExample assembleVideoExampleByDesc(boolean desc){
        VideoExample videoExample = new VideoExample();
        if(desc){
            videoExample.setOrderByClause("modify_time desc");
        } else {
            videoExample.setOrderByClause("modify_time asc");
        }
        return videoExample;
    }
}

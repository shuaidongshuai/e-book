package com.dong.ebook.service.impl;

import com.dong.ebook.dao.VideoTypeDao;
import com.dong.ebook.dto.ResponseGetVideoTypeDto;
import com.dong.ebook.dto.VideoTypeDto;
import com.dong.ebook.model.VideoType;
import com.dong.ebook.model.VideoTypeExample;
import com.dong.ebook.service.VideoTypeService;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VideoTypeServiceImpl implements VideoTypeService {
    @Autowired
    VideoTypeDao videoTypeDao;

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @Override
    public ResponseGetVideoTypeDto getVideoType() {
        List<VideoType> videoTypes = videoTypeDao.selectByExample(new VideoTypeExample());
        ResponseGetVideoTypeDto responseGetVideoTypeDto = new ResponseGetVideoTypeDto();
        responseGetVideoTypeDto.setVideoTypes(dos2dtos(videoTypes));
        responseGetVideoTypeDto.setSuccess(true);
        return responseGetVideoTypeDto;
    }

    public VideoTypeDto do2dto(VideoType videoType){
        return dozerBeanMapper.map(videoType, VideoTypeDto.class);
    }

    public List<VideoTypeDto> dos2dtos(List<VideoType> videoTypes){
        List<VideoTypeDto> videoTypeDtos = new ArrayList<>();
        for(VideoType videoType : videoTypes) {
            VideoTypeDto videoTypeDto = dozerBeanMapper.map(videoType, VideoTypeDto.class);
            videoTypeDtos.add(videoTypeDto);
        }
        return videoTypeDtos;
    }
}

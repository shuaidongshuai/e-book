package com.dong.ebook.service.impl;

import com.dong.ebook.dao.MusicTypeDao;
import com.dong.ebook.dto.ResponseGetMusicTypeDto;
import com.dong.ebook.dto.MusicTypeDto;
import com.dong.ebook.model.MusicType;
import com.dong.ebook.model.MusicTypeExample;
import com.dong.ebook.service.MusicTypeService;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MusicTypeServiceImpl implements MusicTypeService {
    @Autowired
    MusicTypeDao musicTypeDao;

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @Override
    public ResponseGetMusicTypeDto getMusicType() {
        List<MusicType> musicTypes = musicTypeDao.selectByExample(new MusicTypeExample());
        ResponseGetMusicTypeDto responseGetMusicTypeDto = new ResponseGetMusicTypeDto();
        responseGetMusicTypeDto.setMusicTypes(dos2dtos(musicTypes));
        responseGetMusicTypeDto.setSuccess(true);
        return responseGetMusicTypeDto;
    }

    public MusicTypeDto do2dto(MusicType musicType){
        return dozerBeanMapper.map(musicType, MusicTypeDto.class);
    }

    public List<MusicTypeDto> dos2dtos(List<MusicType> musicTypes){
        List<MusicTypeDto> musicTypeDtos = new ArrayList<>();
        for(MusicType musicType : musicTypes) {
            MusicTypeDto musicTypeDto = dozerBeanMapper.map(musicType, MusicTypeDto.class);
            musicTypeDtos.add(musicTypeDto);
        }
        return musicTypeDtos;
    }
}

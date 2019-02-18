package com.dong.ebook.service.impl;

import com.dong.ebook.dao.PictureTypeDao;
import com.dong.ebook.dto.PictureTypeDto;
import com.dong.ebook.dto.ResponseGetPictureTypeDto;
import com.dong.ebook.model.PictureType;
import com.dong.ebook.model.PictureTypeExample;
import com.dong.ebook.service.PictureTypeService;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PictureTypeServiceImpl implements PictureTypeService {
    @Autowired
    PictureTypeDao pictureTypeDao;

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @Override
    public ResponseGetPictureTypeDto getPictureType() {
        List<PictureType> pictureTypes = pictureTypeDao.selectByExample(new PictureTypeExample());
        ResponseGetPictureTypeDto responseGetPictureTypeDto = new ResponseGetPictureTypeDto();
        responseGetPictureTypeDto.setPictureTypes(dos2dtos(pictureTypes));
        responseGetPictureTypeDto.setSuccess(true);
        return responseGetPictureTypeDto;
    }

    public PictureTypeDto do2dto(PictureType pictureType){
        return dozerBeanMapper.map(pictureType, PictureTypeDto.class);
    }

    public List<PictureTypeDto> dos2dtos(List<PictureType> pictureTypes){
        List<PictureTypeDto> pictureTypeDtos = new ArrayList<>();
        for(PictureType pictureType : pictureTypes) {
            PictureTypeDto pictureTypeDto = dozerBeanMapper.map(pictureType, PictureTypeDto.class);
            pictureTypeDtos.add(pictureTypeDto);
        }
        return pictureTypeDtos;
    }
}

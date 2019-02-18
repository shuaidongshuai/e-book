package com.dong.ebook.service.impl;

import com.dong.ebook.dao.BlogTypeDao;
import com.dong.ebook.dto.BlogTypeDto;
import com.dong.ebook.dto.ResponseGetBlogTypeDto;
import com.dong.ebook.model.BlogType;
import com.dong.ebook.model.BlogTypeExample;
import com.dong.ebook.service.BlogTypeService;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BlogTypeServiceImpl implements BlogTypeService {
    @Autowired
    BlogTypeDao blogTypeDao;

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @Override
    public ResponseGetBlogTypeDto getBlogType() {
        List<BlogType> blogTypes = blogTypeDao.selectByExample(new BlogTypeExample());
        ResponseGetBlogTypeDto responseGetBlogTypeDto = new ResponseGetBlogTypeDto();
        responseGetBlogTypeDto.setBlogTypes(dos2dtos(blogTypes));
        responseGetBlogTypeDto.setSuccess(true);
        return responseGetBlogTypeDto;
    }

    public BlogTypeDto do2dto(BlogType blogType){
        return dozerBeanMapper.map(blogType, BlogTypeDto.class);
    }

    public List<BlogTypeDto> dos2dtos(List<BlogType> blogTypes){
        List<BlogTypeDto> blogTypeDtos = new ArrayList<>();
        for(BlogType blogType : blogTypes) {
            BlogTypeDto blogTypeDto = dozerBeanMapper.map(blogType, BlogTypeDto.class);
            blogTypeDtos.add(blogTypeDto);
        }
        return blogTypeDtos;
    }
}

package com.dong.ebook.service.impl;

import com.dong.ebook.dao.BookTypeDao;
import com.dong.ebook.dto.BookTypeDto;
import com.dong.ebook.dto.ResponseGetBookTypeDto;
import com.dong.ebook.model.BookType;
import com.dong.ebook.model.BookTypeExample;
import com.dong.ebook.service.BookTypeService;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookTypeServiceImpl implements BookTypeService {
    @Autowired
    BookTypeDao bookTypeDao;

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @Override
    public ResponseGetBookTypeDto getBookType() {
        List<BookType> bookTypes = bookTypeDao.selectByExample(new BookTypeExample());
        ResponseGetBookTypeDto responseGetBookTypeDto = new ResponseGetBookTypeDto();
        responseGetBookTypeDto.setBookTypes(dos2dtos(bookTypes));
        responseGetBookTypeDto.setSuccess(true);
        return responseGetBookTypeDto;
    }

    public BookTypeDto do2dto(BookType bookType){
        return dozerBeanMapper.map(bookType, BookTypeDto.class);
    }

    public List<BookTypeDto> dos2dtos(List<BookType> bookTypes){
        List<BookTypeDto> bookTypeDtos = new ArrayList<>();
        for(BookType bookType : bookTypes) {
            BookTypeDto bookTypeDto = dozerBeanMapper.map(bookType, BookTypeDto.class);
            bookTypeDtos.add(bookTypeDto);
        }
        return bookTypeDtos;
    }
}

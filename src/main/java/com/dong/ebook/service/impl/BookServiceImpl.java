package com.dong.ebook.service.impl;

import com.dong.ebook.common.UserRole;
import com.dong.ebook.dao.BookDao;
import com.dong.ebook.dto.*;
import com.dong.ebook.model.Book;
import com.dong.ebook.model.BookExample;
import com.dong.ebook.model.BookWithBLOBs;
import com.dong.ebook.model.User;
import com.dong.ebook.security.AuthUserService;
import com.dong.ebook.service.BookService;
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
public class BookServiceImpl implements BookService {
    private static Logger logger = Logger.getLogger(BookServiceImpl.class);

    @Autowired
    private BookDao bookDao;

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @Autowired
    AuthUserService authUserService;

    @Autowired
    UserService userService;

    @Override
    public ResponseCommonDto saveBook(RequestBookDto requestBookDto) {
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(false);

        BookWithBLOBs bookWithBLOBs = RequestBook2doWithBLOBs(requestBookDto);
        if(requestBookDto.getId() == null){
            if (getBookByFileUrl(requestBookDto.getFileUrl()) != null) {
                responseCommonDto.setErrorMsg("此书已经创建");
                return responseCommonDto;
            }
            insertBookWithBLOBs(bookWithBLOBs);
        } else{
            updateBookWithBLOBsById(bookWithBLOBs);
        }
        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    @Override
    public ResponseCommonDto delBook(long id) {
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(false);
        User curUser = authUserService.getCurUser();
        if (!UserRole.SUPERADMIN.equals(curUser.getRole())) {
            responseCommonDto.setErrorMsg("只有超级管理员可以删除图书");
            return responseCommonDto;
        }
        bookDao.deleteByPrimaryKey(id);
        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    @Override
    public ResponseBookListDto getBookList(int pageNum, int pageSize) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        bookDao.selectByExample(new BookExample());
        PageInfo pageInfo = new PageInfo(page.getResult());
        return assembleResponseBookListDto(pageInfo);
    }

    @Override
    public ResponseManagerBookListDto getManagerBookList(int pageNum, int pageSize, boolean desc) {
        return getManagerBookList(pageNum, pageSize, desc, null);
    }

    @Override
    public ResponseManagerBookListDto getManagerBookList(int pageNum, int pageSize, boolean desc, String query) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        BookExample bookExample = assembleBookExampleByDesc(desc);
        if(query != null && !query.isEmpty()){
            bookExample.createCriteria().andNameLike(query);
        }
        bookDao.selectByExample(bookExample);
        PageInfo pageInfo = new PageInfo(page.getResult());
        return assembleResponseManagerBookListDto(pageInfo);
    }

    @Override
    public ResponseBookDto getBook(long id) {
        ResponseBookDto responseBookDto = new ResponseBookDto();
        responseBookDto.setSuccess(false);
        if(id < 1){
            responseBookDto.setErrorMsg("id error");
            return responseBookDto;
        }
        BookWithBLOBs bookWithBLOBs = bookDao.selectByPrimaryKey(id);
        BookDto bookDto = dozerBeanMapper.map(bookWithBLOBs, BookDto.class);
        responseBookDto.setBookDto(bookDto);
        responseBookDto.setSuccess(true);
        return responseBookDto;
    }

    public Book getBookByFileUrl(String fileUrl) {
        BookExample bookExample = new BookExample();
        BookExample.Criteria criteria = bookExample.createCriteria();
        criteria.andFileUrlEqualTo(fileUrl);
        List<Book> books = bookDao.selectByExample(bookExample);
        if (books.size() > 0) {
            return books.get(0);
        }
        return null;
    }

    public void insertBookWithBLOBs(BookWithBLOBs bookWithBLOBs) {
        Date date = new Date();
        bookWithBLOBs.setModifyUserId(authUserService.getCurUser().getId());
        bookWithBLOBs.setCreateTime(date);
        bookWithBLOBs.setModifyTime(date);
        bookDao.insert(bookWithBLOBs);
    }

    public void updateBookWithBLOBsById(BookWithBLOBs bookWithBLOBs) {
        bookWithBLOBs.setModifyUserId(authUserService.getCurUser().getId());
        bookWithBLOBs.setModifyTime(new Date());
        bookDao.updateByPrimaryKeySelective(bookWithBLOBs);
    }

    public BookWithBLOBs RequestBook2doWithBLOBs(RequestBookDto requestBookDto) {
        BookWithBLOBs bookWithBLOBs = dozerBeanMapper.map(requestBookDto, BookWithBLOBs.class);
        return bookWithBLOBs;
    }

    public BookDto do2dto(Book book){
        return dozerBeanMapper.map(book, BookDto.class);
    }

    public List<BookDto> dos2dtos(List<Book> books){
        List<BookDto> bookDtos = new ArrayList<>();
        for(Book book : books){
            bookDtos.add(do2dto(book));
        }
        return bookDtos;
    }

    public ResponseBookListDto assembleResponseBookListDto(PageInfo pageInfo) {
        List<BookDto> bookDtos = dos2dtos(pageInfo.getList());

        pageInfo.setList(bookDtos);

        ResponseBookListDto responseBookListDto = new ResponseBookListDto();
        responseBookListDto.setPageInfo(pageInfo);
        responseBookListDto.setSuccess(true);
        return responseBookListDto;
    }

    public ResponseManagerBookListDto assembleResponseManagerBookListDto(PageInfo pageInfo) {
        List<Book> books = pageInfo.getList();
        List<ManagerBookDto> managerBookDtos = new ArrayList<>();
        for(Book book : books){
            ManagerBookDto managerBookDto = dozerBeanMapper.map(book, ManagerBookDto.class);
            managerBookDtos.add(managerBookDto);

            String userNickname = userService.findUserById(book.getModifyUserId()).getNickname();
            managerBookDto.setModifyUserNickname(userNickname);
        }
        pageInfo.setList(managerBookDtos);

        ResponseManagerBookListDto responseManagerBookListDto = new ResponseManagerBookListDto();
        responseManagerBookListDto.setPageInfo(pageInfo);
        responseManagerBookListDto.setSuccess(true);
        return responseManagerBookListDto;
    }

    public BookExample assembleBookExampleByDesc(boolean desc){
        BookExample bookExample = new BookExample();
        if(desc){
            bookExample.setOrderByClause("modify_time desc");
        } else {
            bookExample.setOrderByClause("modify_time asc");
        }
        return bookExample;
    }
}

package com.dong.ebook.service.impl;

import com.dong.ebook.common.PreferenceTypeName;
import com.dong.ebook.common.UserRole;
import com.dong.ebook.dao.BookDao;
import com.dong.ebook.dto.*;
import com.dong.ebook.model.*;
import com.dong.ebook.security.AuthUserService;
import com.dong.ebook.service.BookService;
import com.dong.ebook.service.ElasticsearchService;
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

    @Autowired
    PreferenceService preferenceService;

    @Autowired
    private ElasticsearchService elasticsearchService;

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
        elasticsearchService.delBook(id);
        responseCommonDto.setSuccess(true);
        return responseCommonDto;
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

    @Override
    public ResponseMainPageBookListDto getMainPageBookList() {
        int bigSize = 1, smallSize = 12;
        int totalSize = bigSize + smallSize;
        List<Book> books;
        User user = authUserService.getCurUser();
        if(user == null){
            books = getBookList(1, totalSize, true);
        }else{
            //根据兴趣爱好找
            List<Long> typeIdList = preferenceService.getPreferenceTypeId(user.getId(), PreferenceTypeName.BOOK);
            books = getBookListByTypeId(1, totalSize, true, typeIdList);
            if(books.size() < totalSize){
                //数量不够就找别的
                books.addAll(getBookListByNotTypeId(1, totalSize - books.size(), true, typeIdList));
            }
        }
        if(books.size() > totalSize){
            logger.info("getMainPageVideoList books.size()=" + books.size() + " > size=" + totalSize);
            books = books.subList(0, totalSize);
        }
        return assembleResponseMainPageBookListDto(books, bigSize, smallSize);
    }

    public List<Book> getBookList(int pageNum, int pageSize, boolean desc) {
        Page<Book> page = PageHelper.startPage(pageNum, pageSize);
        BookExample bookExample = assembleBookExampleByDesc(desc);
        bookDao.selectByExample(bookExample);
        return page.getResult();
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
        elasticsearchService.addBook(BookWithBLOBs2Elasticsearch(bookWithBLOBs));
    }

    public void updateBookWithBLOBsById(BookWithBLOBs bookWithBLOBs) {
        bookWithBLOBs.setModifyUserId(authUserService.getCurUser().getId());
        bookWithBLOBs.setModifyTime(new Date());
        bookDao.updateByPrimaryKeySelective(bookWithBLOBs);
        elasticsearchService.updateBook(BookWithBLOBs2Elasticsearch(bookWithBLOBs));
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

    public ResponseMainPageBookListDto assembleResponseMainPageBookListDto(List<Book> books, int bigSize, int smallSize) {
        List<BookDto> bigBookDtos = new ArrayList<>(bigSize);
        List<BookDto> smallBookDtos = new ArrayList<>(smallSize);
        int idx = 0;
        for(Book book : books){
            //删除不需要的数据
            book.setCreateTime(null);
            book.setModifyTime(null);
            book.setBookTypeId(null);
            book.setModifyUserId(null);

            if(++idx <= bigSize){
                bigBookDtos.add(do2dto(book));
            }else{
                smallBookDtos.add(do2dto(book));
            }
        }

        ResponseMainPageBookListDto responseMainPageBookListDto = new ResponseMainPageBookListDto();
        responseMainPageBookListDto.setBigBookDtos(bigBookDtos);
        responseMainPageBookListDto.setSmallBookDtos(smallBookDtos);
        return responseMainPageBookListDto;
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

    public ElasticsearchBookDto BookWithBLOBs2Elasticsearch(BookWithBLOBs bookWithBLOBs){
        return dozerBeanMapper.map(bookWithBLOBs, ElasticsearchBookDto.class);
    }

    public List<Book> getBookListByTypeId(int pageNum, int pageSize, boolean desc, List<Long> typeIds) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        BookExample bookExample = assembleBookExampleByDesc(desc);
        if(typeIds.size() > 0){
            bookExample.createCriteria().andBookTypeIdIn(typeIds);
        }
        bookDao.selectByExample(bookExample);
        return page.getResult();
    }

    public List<Book> getBookListByNotTypeId(int pageNum, int pageSize, boolean desc, List<Long> typeIds) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        BookExample bookExample = assembleBookExampleByDesc(desc);
        if(typeIds.size() > 0){
            bookExample.createCriteria().andBookTypeIdNotIn(typeIds);
        }
        bookDao.selectByExample(bookExample);
        return page.getResult();
    }
}

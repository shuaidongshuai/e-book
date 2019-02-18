package com.dong.ebook.service;

import com.dong.ebook.dto.*;

public interface BookService {
    ResponseCommonDto saveBook(RequestBookDto requestBookDto);
    ResponseCommonDto delBook(long id);
    ResponseBookDto getBook(long id);
    ResponseManagerBookListDto getManagerBookList(int pageNum, int pageSize, boolean desc);
    ResponseManagerBookListDto getManagerBookList(int pageNum, int pageSize, boolean desc, String query);
    ResponseBookListDto getBookList(int pageNum, int pageSize);
}

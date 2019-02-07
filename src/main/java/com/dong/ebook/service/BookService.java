package com.dong.ebook.service;

import com.dong.ebook.dto.ResponseCommonDto;
import com.dong.ebook.dto.ResponseSaveBookDto;

public interface BookService {
    ResponseSaveBookDto addBook(String bookUrl);
    ResponseCommonDto saveBook(String bookUrl);
    ResponseCommonDto delBookById(Long id);
}

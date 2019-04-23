package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseMobileMainPageBookListDto extends ResponseCommonDto{
    private List<BookDto> books;
}

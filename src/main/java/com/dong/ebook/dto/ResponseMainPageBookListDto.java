package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseMainPageBookListDto extends ResponseCommonDto{
    private List<BookDto> bigBookDtos;
    private List<BookDto> smallBookDtos;
}

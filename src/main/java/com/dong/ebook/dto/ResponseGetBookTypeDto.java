package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseGetBookTypeDto extends ResponseCommonDto {
    private List<BookTypeDto> bookTypes;
}

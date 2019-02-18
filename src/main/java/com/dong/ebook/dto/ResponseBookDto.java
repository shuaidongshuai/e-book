package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseBookDto extends ResponseCommonDto{
    private BookDto bookDto;
}

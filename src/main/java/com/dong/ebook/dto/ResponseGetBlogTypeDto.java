package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseGetBlogTypeDto extends ResponseCommonDto {
    private List<BlogTypeDto> blogTypes;
}

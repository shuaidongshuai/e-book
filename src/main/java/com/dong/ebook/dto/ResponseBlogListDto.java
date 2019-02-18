package com.dong.ebook.dto;

import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseBlogListDto extends ResponseCommonDto{
    private PageInfo<BlogDto> pageInfo;
}

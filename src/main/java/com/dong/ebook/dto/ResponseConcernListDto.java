package com.dong.ebook.dto;

import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseConcernListDto extends ResponseCommonDto{
    private PageInfo<ConcernUserDto> pageInfo;
}

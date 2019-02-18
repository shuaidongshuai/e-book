package com.dong.ebook.dto;

import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseManagerVideoListDto extends ResponseCommonDto{
    private PageInfo<ManagerVideoDto> pageInfo;
}

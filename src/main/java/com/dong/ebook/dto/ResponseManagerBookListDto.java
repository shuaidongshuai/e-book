package com.dong.ebook.dto;

import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseManagerBookListDto extends ResponseCommonDto{
    private PageInfo<ManagerBookDto> pageInfo;
}

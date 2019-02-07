package com.dong.ebook.dto;

import com.github.pagehelper.PageInfo;

public class ResponseUserListDto extends ResponseCommonDto {
    private PageInfo<UserDto> pageInfo;

    public PageInfo<UserDto> getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo<UserDto> pageInfo) {
        this.pageInfo = pageInfo;
    }
}

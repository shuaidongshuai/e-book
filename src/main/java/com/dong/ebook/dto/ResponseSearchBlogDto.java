package com.dong.ebook.dto;

import com.github.pagehelper.PageInfo;

public class ResponseSearchBlogDto extends ResponseCommonDto {
    private PageInfo<SearchBlogDto> pageInfo;

    public PageInfo<SearchBlogDto> getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo<SearchBlogDto> pageInfo) {
        this.pageInfo = pageInfo;
    }
}

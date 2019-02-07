package com.dong.ebook.dto;

import com.github.pagehelper.PageInfo;

public class ResponseBlogListDto extends ResponseCommonDto{
    private PageInfo<BlogDto> pageInfo;

    public PageInfo<BlogDto> getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo<BlogDto> pageInfo) {
        this.pageInfo = pageInfo;
    }
}

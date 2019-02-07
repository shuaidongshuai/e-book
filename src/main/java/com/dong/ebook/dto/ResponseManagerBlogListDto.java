package com.dong.ebook.dto;

import com.github.pagehelper.PageInfo;

public class ResponseManagerBlogListDto extends ResponseCommonDto{
    private PageInfo<ManagerBlogDto> pageInfo;

    public PageInfo<ManagerBlogDto> getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo<ManagerBlogDto> pageInfo) {
        this.pageInfo = pageInfo;
    }
}

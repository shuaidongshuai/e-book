package com.dong.ebook.service;

import com.dong.ebook.dto.ElasticsearchBlogDto;
import com.dong.ebook.dto.ResponseSearchBlogDto;

public interface ElasticsearchService {
    void addBlog(ElasticsearchBlogDto elasticsearchBlogDto);
    void delBlog(Long id);
    void updateBlog(ElasticsearchBlogDto elasticsearchBlogDto);
    ElasticsearchBlogDto getBlog(Long id);
    ResponseSearchBlogDto getBlog(String query, Integer pageNum, Integer pageSize);
}

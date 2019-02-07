package com.dong.ebook.controller;

import com.dong.ebook.dto.ResponseSearchBlogDto;
import com.dong.ebook.service.ElasticsearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/search")
public class SearchController {
    @Autowired
    ElasticsearchService elasticsearchService;

    @GetMapping("/blog")
    public String blog(String query, Integer pageNum, Integer pageSize,  Model model) {
        ResponseSearchBlogDto responseSearchBlogDto = elasticsearchService.getBlog(query, pageNum, pageSize);
        model.addAttribute("pageInfo", responseSearchBlogDto.getPageInfo());
        model.addAttribute("query", query);
        return "/search/blog";
    }

}

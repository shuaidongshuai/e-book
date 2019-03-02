package com.dong.ebook.controller;

import com.dong.ebook.dto.*;
import com.dong.ebook.service.BlogService;
import com.dong.ebook.service.ElasticsearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/search")
public class SearchController {
    @Autowired
    ElasticsearchService elasticsearchService;

    @Autowired
    BlogService blogService;

    @GetMapping("/")
    public String search(String query, Integer pageNum, Integer pageSize, Model model) {
        pageSize /= 4;
        ResponseSearchBlogDto responseSearchBlogDto = elasticsearchService.getBlog(query, pageNum, pageSize);
        ResponseSearchBookDto responseSearchBookDto = elasticsearchService.getBook(query, pageNum, pageSize);
        ResponseSearchVideoDto responseSearchVideoDto = elasticsearchService.getVideo(query, pageNum, pageSize);
        ResponseSearchMusicDto responseSearchMusicDto = elasticsearchService.getMusic(query, pageNum, pageSize);
        ResponseSearchPictureDto responseSearchPictureDto = elasticsearchService.getPicture(query, pageNum, pageSize);

        model.addAttribute("blogPageInfo", responseSearchBlogDto.getPageInfo());
        model.addAttribute("bookPageInfo", responseSearchBookDto.getPageInfo());
        model.addAttribute("videoPageInfo", responseSearchVideoDto.getPageInfo());
        model.addAttribute("musicPageInfo", responseSearchMusicDto.getPageInfo());
        model.addAttribute("picturePageInfo", responseSearchPictureDto.getPageInfo());
        return "search/index";
    }

    @GetMapping("/blog")
    public String blog(String query, Integer pageNum, Integer pageSize, Model model) {
        ResponseSearchBlogDto responseSearchBlogDto = elasticsearchService.getBlog(query, pageNum, pageSize);
        List<BlogDto> hotBlogs = blogService.getHotBlog(6);
        model.addAttribute("pageInfo", responseSearchBlogDto.getPageInfo());
        model.addAttribute("hotBlogs", hotBlogs);
        model.addAttribute("query", query);
        return "search/blog";
    }

    @GetMapping("/book")
    public String book(String query, Integer pageNum, Integer pageSize, Model model) {
        ResponseSearchBookDto responseSearchBookDto = elasticsearchService.getBook(query, pageNum, pageSize);
        model.addAttribute("pageInfo", responseSearchBookDto.getPageInfo());
        model.addAttribute("query", query);
        return "search/book";
    }

    @GetMapping("/video")
    public String video(String query, Integer pageNum, Integer pageSize, Model model) {
        ResponseSearchVideoDto responseSearchVideoDto = elasticsearchService.getVideo(query, pageNum, pageSize);
        model.addAttribute("pageInfo", responseSearchVideoDto.getPageInfo());
        model.addAttribute("query", query);
        return "search/video";
    }

    @GetMapping("/music")
    public String music(String query, Integer pageNum, Integer pageSize, Model model) {
        ResponseSearchMusicDto responseSearchMusicDto = elasticsearchService.getMusic(query, pageNum, pageSize);
        model.addAttribute("pageInfo", responseSearchMusicDto.getPageInfo());
        model.addAttribute("query", query);
        return "search/music";
    }


    @GetMapping("/picture")
    public String picture(String query, Integer pageNum, Integer pageSize, Model model) {
        ResponseSearchPictureDto responseSearchPictureDto = elasticsearchService.getPicture(query, pageNum, pageSize);
        model.addAttribute("pageInfo", responseSearchPictureDto.getPageInfo());
        model.addAttribute("query", query);
        return "search/picture";
    }


}

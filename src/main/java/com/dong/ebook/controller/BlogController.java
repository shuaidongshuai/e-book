package com.dong.ebook.controller;

import com.dong.ebook.dto.ResponseBlogPageDto;
import com.dong.ebook.dto.ResponseUserBlogListDto;
import com.dong.ebook.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/blog")
public class BlogController {
    @Autowired
    private BlogService blogService;

    @GetMapping("/{blogId}")
    public String blog(@PathVariable("blogId") Long blogId, Model model) {
        ResponseBlogPageDto responseBlogPageDto = blogService.getUserBlogById(blogId);
        model.addAttribute("userBlog", responseBlogPageDto);
        return "blog/userBlog";
    }

    @GetMapping("/userBlogList")
    public String userBlogList(Long userId, Integer pageNum, Integer pageSize,  Model model){
        ResponseUserBlogListDto responseUserBlogListDto = blogService.getUserBlogList(userId, pageNum, pageSize);
        model.addAttribute("pageInfo", responseUserBlogListDto.getPageInfo());
        model.addAttribute("userBlogs", responseUserBlogListDto);
        return "blog/userBlogList";
    }

}

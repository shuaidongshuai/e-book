package com.dong.ebook.controller;

import com.dong.ebook.dto.*;
import com.dong.ebook.service.BlogService;
import com.dong.ebook.service.OssService;
import com.dong.ebook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    UserService userService;

    @Autowired
    BlogService blogService;

    @Autowired
    OssService ossService;

    @GetMapping("/manager")
    public String manager(Model model) {
        List<Menu> menus = new ArrayList<>();
        menus.add(new Menu("用户管理", "/admin/userList"));
        menus.add(new Menu("博客管理", "/admin/blogList"));
        model.addAttribute("menus", menus);
        return "/admin/manager";
    }

    @GetMapping("/userList")
    public String userList(int pageNum, int pageSize, Model model) {
        ResponseUserListDto responseUserListDto = userService.managerFindList(pageNum, pageSize);
        model.addAttribute("pageInfo", responseUserListDto.getPageInfo());
        model.addAttribute("users", responseUserListDto.getPageInfo().getList());
        return "/admin/userList";
    }

    @GetMapping("/findUsername")
    public String findUsername(int pageNum, int pageSize, String username, Model model) {
        ResponseUserListDto responseUserListDto = userService.managerFindList(pageNum, pageSize, username);
        model.addAttribute("pageInfo", responseUserListDto.getPageInfo());
        model.addAttribute("users", responseUserListDto.getPageInfo().getList());
        return "/admin/userList :: #userListReplace";
    }

    @GetMapping("/blogList")
    public String blogList(int pageNum, int pageSize, Model model) {
        ResponseManagerBlogListDto responseManagerBlogListDto = blogService.getManagerBlogList(pageNum, pageSize);
        model.addAttribute("pageInfo", responseManagerBlogListDto.getPageInfo());
        return "/admin/blogList";
    }

    @GetMapping("/BlogListLike")
    public String findBlogListLike(int pageNum, int pageSize, String query, Model model) {
        ResponseManagerBlogListDto responseManagerBlogListDto = blogService.getManagerBlogList(pageNum, pageSize, query);
        model.addAttribute("pageInfo", responseManagerBlogListDto.getPageInfo());
        return "/admin/blogList :: #blogListReplace";
    }

    @GetMapping("/changeStatus")
    @ResponseBody
    public ResponseCommonDto changeStatus(Long userId, String userStatus) {
        ResponseCommonDto responseCommonDto = userService.changeStatus(userId, userStatus);
        return responseCommonDto;
    }

    @GetMapping("/changeRole")
    @ResponseBody
    public ResponseCommonDto changeRole(Long userId, String userRole) {
        ResponseCommonDto responseCommonDto = userService.changeRole(userId, userRole);
        return responseCommonDto;
    }

    /**
     * 为了使用JQuery.filer做的适配
     * @return
     */
    @RequestMapping("/uploadAdapt")
    @ResponseBody
    public String uploadAdapt() {
        return "";
    }

    @GetMapping("/uploadBook")
    @ResponseBody
    public ResponseUploadDto uploadBook(String filename) throws UnsupportedEncodingException {
        ResponseUploadDto responseUploadDto = ossService.uploadBook(filename);
        return responseUploadDto;
    }

    @GetMapping("/uploadVideo")
    @ResponseBody
    public ResponseUploadDto uploadVideo(String filename) throws UnsupportedEncodingException {
        ResponseUploadDto responseUploadDto = ossService.uploadVideo(filename);
        return responseUploadDto;
    }

    @GetMapping("/uploadMusic")
    @ResponseBody
    public ResponseUploadDto uploadMusic(String filename) throws UnsupportedEncodingException {
        ResponseUploadDto responseUploadDto = ossService.uploadMusic(filename);
        return responseUploadDto;
    }

    @GetMapping("/uploadPicture")
    @ResponseBody
    public ResponseUploadDto uploadPicture(String filename) throws UnsupportedEncodingException {
        ResponseUploadDto responseUploadDto = ossService.uploadPicture(filename);
        return responseUploadDto;
    }

    @GetMapping("/saveBook")
    @ResponseBody
    public ResponseCommonDto saveBook(String bookUrl) {
        return null;
    }

    @GetMapping("/upload")
    public String upload() {
        return "/admin/upload";
    }

}

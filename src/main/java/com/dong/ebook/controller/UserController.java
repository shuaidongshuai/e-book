package com.dong.ebook.controller;

import com.dong.ebook.dto.*;
import com.dong.ebook.model.User;
import com.dong.ebook.security.AuthUserService;
import com.dong.ebook.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthUserService authUserService;

    @Autowired
    private OssService ossService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private ConcernService concernService;

    @RequestMapping("/list")
    public String listUser(Model model) {
        model.addAttribute("users", userService);
        return "/list";
    }

    @DeleteMapping("/del/{id}")
    public String delUser(@PathVariable("id") Long id, Model model) {
//        userService.delete(id);
        return "/";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        User user = authUserService.getCurUser();
        UserDto userDto = userService.user2dto(user);
        model.addAttribute("user", userDto);
        return "/user/profile";
    }

    @GetMapping("/profile/afterSave")
    public String profileAfterSave(Model model) {
        profile(model);
        model.addAttribute("afterSave", true);
        return "/user/profile";
    }

    @PostMapping("/profileSave")
    public String profileSave(RequestUserDto requestUserDto) {
        userService.updateById(requestUserDto);
        return "redirect:/user/profile/afterSave";
    }

    @GetMapping("/avatarEdit")
    public String avatarEdit() {
        return "/user/avatarEdit";
    }

    @GetMapping("/uploadAvatar")
    @ResponseBody
    public ResponseUploadDto uploadAvatar() throws UnsupportedEncodingException {
        User user = authUserService.getCurUser();
        ResponseUploadDto responseUploadDto = ossService.uploadAvatar(user);
        return responseUploadDto;
    }

    @GetMapping("/saveAvatar")
    @ResponseBody
    public String saveAvatar(String fileUrl) {
        User user = authUserService.getCurUser();
        userService.saveAvatar(user.getId(), fileUrl);
        return "";
    }

    @GetMapping("/blogCreate")
    public String blogCreate(Model model) {
        ResponseBlogEditDto responseBlogEditDto = blogService.editBlog(null);
        model.addAttribute("blog", responseBlogEditDto);
        return "/user/blogEdit";
    }

    @GetMapping("/blogEdit/{blogId}")
    public String blogEdit(@PathVariable("blogId") Long blogId, Model model) {
        ResponseBlogEditDto responseBlogEditDto = blogService.editBlog(blogId);
        model.addAttribute("blog", responseBlogEditDto);
        return "/user/blogEdit";
    }

    @GetMapping("/blogImgUpload")
    @ResponseBody
    public ResponseUploadDto blogImgUpload() throws UnsupportedEncodingException {
        User user = authUserService.getCurUser();
        ResponseUploadDto responseUploadDto = ossService.uploadBlogImage(user);
        return responseUploadDto;
    }

    @PostMapping("/blogSave")
    @ResponseBody
    public ResponseBlogSaveDto blogSave(BlogDto blogDto) {
        ResponseBlogSaveDto responseBlogSaveDto = blogService.saveBlog(blogDto);
        return responseBlogSaveDto;
    }

    @PutMapping("/concern/{userId}")
    @ResponseBody
    public ResponseCommonDto concern(@PathVariable("userId") Long userId) {
        ResponseCommonDto responseCommonDto = concernService.concernUser(userId);
        return responseCommonDto;
    }

    @DeleteMapping("/cancelConcern/{userId}")
    @ResponseBody
    public ResponseCommonDto cancelConcern(@PathVariable("userId") Long userId) {
        ResponseCommonDto responseCommonDto = concernService.cancelConcernUser(userId);
        return responseCommonDto;
    }

    @PutMapping("/vote/{blogId}")
    @ResponseBody
    public ResponseCommonDto vote(@PathVariable("blogId") Long blogId) {
        ResponseCommonDto responseCommonDto = blogService.voteBlog(blogId);
        return responseCommonDto;
    }

    @DeleteMapping("/cancelVote/{blogId}")
    @ResponseBody
    public ResponseCommonDto cancelVote(@PathVariable("blogId") Long blogId) {
        ResponseCommonDto responseCommonDto = blogService.cancelVoteBlog(blogId);
        return responseCommonDto;
    }

    @DeleteMapping("/deleteBlog/{blogId}")
    @ResponseBody
    public ResponseCommonDto deleteBlog(@PathVariable("blogId") Long blogId) {
        ResponseCommonDto responseCommonDto = blogService.deleteBlog(blogId);
        return responseCommonDto;
    }

}

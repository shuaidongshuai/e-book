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
    private BlogTypeService blogTypeService;

    @Autowired
    private PreferenceService preferenceService;

    @Autowired
    private ConcernService concernService;

    @Autowired
    private VideoService videoService;

    @RequestMapping("/list")
    public String listUser(Model model) {
        model.addAttribute("users", userService);
        return "list";
    }

    @DeleteMapping("/del/{id}")
    public String delUser(@PathVariable("id") Long id, Model model) {
//        userService.delUser(id);
        return "";
    }

    @GetMapping("/personal")
    public String personal(Model model) {
        User curUser = authUserService.getCurUser();
        UserDto userDto = userService.user2dto(curUser);
        model.addAttribute("user", userDto);
        return "user/personal";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        User user = authUserService.getCurUser();
        UserDto userDto = userService.user2dto(user);
        model.addAttribute("user", userDto);
        return "user/profile";
    }

    @PostMapping("/profileSave")
    @ResponseBody
    public ResponseCommonDto profileSave(RequestUserDto requestUserDto) {
        ResponseCommonDto responseCommonDto = userService.updateUserById(requestUserDto);
        return responseCommonDto;
    }

    @GetMapping("/avatarEdit")
    public String avatarEdit() {
        return "user/avatarEdit";
    }

    @GetMapping("/uploadAvatar")
    @ResponseBody
    public ResponseUploadDto uploadAvatar() throws UnsupportedEncodingException {
        ResponseUploadDto responseUploadDto = ossService.uploadAvatar();
        return responseUploadDto;
    }

    @GetMapping("/saveAvatar")
    @ResponseBody
    public String saveAvatar(String fileUrl) {
        User user = authUserService.getCurUser();
        userService.saveAvatar(user.getId(), fileUrl);
        return "";
    }

    @GetMapping("/preferenceBook")
    public String preferenceBook(Model model) {
        ResponsePreferenceDto responsePreferenceDto = preferenceService.getBookPreference();
        model.addAttribute("preferenceDtos", responsePreferenceDto.getPreferenceDtos());
        return "user/preferenceBook";
    }

    @PutMapping("/addBookTag/{typeId}")
    @ResponseBody
    public ResponseCommonDto addBookTag(@PathVariable("typeId") Long typeId) {
        ResponseCommonDto responseCommonDto = preferenceService.addBookPreference(typeId);
        return responseCommonDto;
    }

    @DeleteMapping("/delBookTag/{typeId}")
    @ResponseBody
    public ResponseCommonDto delBookTag(@PathVariable("typeId") Long typeId) {
        ResponseCommonDto responseCommonDto = preferenceService.delBookPreference(typeId);
        return responseCommonDto;
    }

    @GetMapping("/preferenceVideo")
    public String preferenceVideo(Model model) {
        ResponsePreferenceDto responsePreferenceDto = preferenceService.getVideoPreference();
        model.addAttribute("preferenceDtos", responsePreferenceDto.getPreferenceDtos());
        return "user/preferenceVideo";
    }

    @PutMapping("/addVideoTag/{typeId}")
    @ResponseBody
    public ResponseCommonDto addVideoTag(@PathVariable("typeId") Long typeId) {
        ResponseCommonDto responseCommonDto = preferenceService.addVideoPreference(typeId);
        return responseCommonDto;
    }

    @DeleteMapping("/delVideoTag/{typeId}")
    @ResponseBody
    public ResponseCommonDto delVideoTag(@PathVariable("typeId") Long typeId) {
        ResponseCommonDto responseCommonDto = preferenceService.delVideoPreference(typeId);
        return responseCommonDto;
    }

    @GetMapping("/videoPlay/{id}")
    public String videoPlay(@PathVariable("id") Long id, Model model) {
        ResponseVideoDto responseVideoDto = videoService.getVideo(id);
        model.addAttribute("video", responseVideoDto.getVideoDto());
        return "user/videoPlay";
    }

    @GetMapping("/preferenceMusic")
    public String preferenceMusic(Model model) {
        ResponsePreferenceDto responsePreferenceDto = preferenceService.getMusicPreference();
        model.addAttribute("preferenceDtos", responsePreferenceDto.getPreferenceDtos());
        return "user/preferenceMusic";
    }

    @PutMapping("/addMusicTag/{typeId}")
    @ResponseBody
    public ResponseCommonDto addMusicTag(@PathVariable("typeId") Long typeId) {
        ResponseCommonDto responseCommonDto = preferenceService.addMusicPreference(typeId);
        return responseCommonDto;
    }

    @DeleteMapping("/delMusicTag/{typeId}")
    @ResponseBody
    public ResponseCommonDto delMusicTag(@PathVariable("typeId") Long typeId) {
        ResponseCommonDto responseCommonDto = preferenceService.delMusicPreference(typeId);
        return responseCommonDto;
    }

    @GetMapping("/preferencePicture")
    public String preferencePicture(Model model) {
        ResponsePreferenceDto responsePreferenceDto = preferenceService.getPicturePreference();
        model.addAttribute("preferenceDtos", responsePreferenceDto.getPreferenceDtos());
        return "user/preferencePicture";
    }

    @PutMapping("/addPictureTag/{typeId}")
    @ResponseBody
    public ResponseCommonDto addPictureTag(@PathVariable("typeId") Long typeId) {
        ResponseCommonDto responseCommonDto = preferenceService.addPicturePreference(typeId);
        return responseCommonDto;
    }

    @DeleteMapping("/delPictureTag/{typeId}")
    @ResponseBody
    public ResponseCommonDto delPictureTag(@PathVariable("typeId") Long typeId) {
        ResponseCommonDto responseCommonDto = preferenceService.delPicturePreference(typeId);
        return responseCommonDto;
    }

    @GetMapping("/preferenceBlog")
    public String preferenceBlog(Model model) {
        ResponsePreferenceDto responsePreferenceDto = preferenceService.getBlogPreference();
        model.addAttribute("preferenceDtos", responsePreferenceDto.getPreferenceDtos());
        return "user/preferenceBlog";
    }

    @PutMapping("/addBlogTag/{typeId}")
    @ResponseBody
    public ResponseCommonDto addBlogTag(@PathVariable("typeId") Long typeId) {
        ResponseCommonDto responseCommonDto = preferenceService.addBlogPreference(typeId);
        return responseCommonDto;
    }

    @DeleteMapping("/delBlogTag/{typeId}")
    @ResponseBody
    public ResponseCommonDto delBlogTag(@PathVariable("typeId") Long typeId) {
        ResponseCommonDto responseCommonDto = preferenceService.delBlogPreference(typeId);
        return responseCommonDto;
    }

    @GetMapping("/blogCreate")
    public String blogCreate(Model model) {
        ResponseBlogEditDto responseBlogEditDto = blogService.editBlog(0);
        ResponseGetBlogTypeDto responseGetBlogTypeDto = blogTypeService.getBlogType();
        model.addAttribute("blog", responseBlogEditDto);
        model.addAttribute("blogTypes", responseGetBlogTypeDto.getBlogTypes());
        return "user/blogEdit";
    }

    @GetMapping("/blogEdit/{blogId}")
    public String blogEdit(@PathVariable("blogId") Long blogId, Model model) {
        ResponseBlogEditDto responseBlogEditDto = blogService.editBlog(blogId);
        ResponseGetBlogTypeDto responseGetBlogTypeDto = blogTypeService.getBlogType();
        model.addAttribute("blog", responseBlogEditDto);
        model.addAttribute("blogTypes", responseGetBlogTypeDto.getBlogTypes());
        return "user/blogEdit";
    }

    @GetMapping("/blogImgUpload")
    @ResponseBody
    public ResponseUploadDto blogImgUpload() throws UnsupportedEncodingException {
        ResponseUploadDto responseUploadDto = ossService.uploadBlogImage();
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

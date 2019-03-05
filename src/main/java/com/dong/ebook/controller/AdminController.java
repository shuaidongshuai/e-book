package com.dong.ebook.controller;

import com.dong.ebook.common.InitEs;
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
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    UserService userService;

    @Autowired
    BlogService blogService;

    @Autowired
    OssService ossService;

    @Autowired
    BookService bookService;

    @Autowired
    BookTypeService bookTypeService;

    @Autowired
    VideoService videoService;

    @Autowired
    VideoTypeService videoTypeService;

    @Autowired
    MusicService musicService;

    @Autowired
    MusicTypeService musicTypeService;

    @Autowired
    PictureService pictureService;

    @Autowired
    PictureTypeService pictureTypeService;

    @Autowired
    AuthUserService authUserService;

    @Autowired
    PageViewService pageViewService;

    @Autowired
    HotWordsService hotWordsService;

    @Autowired
    InitEs initEs;

    @GetMapping("/manager")
    public String manager(Model model) {
        User curUser = authUserService.getCurUser();
        model.addAttribute("user", curUser);
        return "admin/manager";
    }

    @GetMapping("/userList")
    public String userList(int pageNum, int pageSize, Model model) {
        ResponseUserListDto responseUserListDto = userService.managerFindList(pageNum, pageSize);
        model.addAttribute("pageInfo", responseUserListDto.getPageInfo());
        model.addAttribute("users", responseUserListDto.getPageInfo().getList());
        return "admin/userList";
    }

    @GetMapping("/findUsername")
    public String findUsername(int pageNum, int pageSize, String username, Model model) {
        ResponseUserListDto responseUserListDto = userService.managerFindList(pageNum, pageSize, username);
        model.addAttribute("pageInfo", responseUserListDto.getPageInfo());
        model.addAttribute("users", responseUserListDto.getPageInfo().getList());
        return "admin/userList :: #userListReplace";
    }

    @GetMapping("/blogList")
    public String blogList(int pageNum, int pageSize, boolean desc, Model model) {
        ResponseManagerBlogListDto responseManagerBlogListDto = blogService.getManagerBlogList(pageNum, pageSize, desc);
        model.addAttribute("pageInfo", responseManagerBlogListDto.getPageInfo());
        model.addAttribute("desc", desc);
        return "admin/blogList";
    }

    @GetMapping("/BlogListLike")
    public String BlogListLike(int pageNum, int pageSize, boolean desc, String query, Model model) {
        ResponseManagerBlogListDto responseManagerBlogListDto = blogService.getManagerBlogList(pageNum, pageSize, desc, query);
        model.addAttribute("pageInfo", responseManagerBlogListDto.getPageInfo());
        model.addAttribute("desc", desc);
        return "admin/blogList :: #blogListReplace";
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
     * 调试js使用
     */
    @RequestMapping("/uploadTest")
    public String uploadAdapt(Model model) {
        ResponseGetBookTypeDto responseGetBookTypeDto = bookTypeService.getBookType();
        model.addAttribute("bookTypes", responseGetBookTypeDto.getBookTypes());
        return "admin/uploadTest";
    }

    /**
     * 图书
     * @param model
     * @return
     */
    @GetMapping("/uploadBook")
    public String uploadBook(Model model) {
        ResponseGetBookTypeDto responseGetBookTypeDto = bookTypeService.getBookType();
        model.addAttribute("bookTypes", responseGetBookTypeDto.getBookTypes());
        return "admin/uploadBook";
    }

    @PostMapping("/saveBook")
    @ResponseBody
    public ResponseCommonDto saveBook(RequestBookDto requestBookDto) {
        ResponseCommonDto responseCommonDto = bookService.saveBook(requestBookDto);
        return responseCommonDto;
    }

    @GetMapping("/bookList")
    public String bookList(int pageNum, int pageSize, boolean desc, Model model) {
        ResponseManagerBookListDto responseManagerBookListDto = bookService.getManagerBookList(pageNum, pageSize, desc);
        ResponseGetBookTypeDto responseGetBookTypeDto = bookTypeService.getBookType();
        model.addAttribute("pageInfo", responseManagerBookListDto.getPageInfo());
        model.addAttribute("bookTypes", responseGetBookTypeDto.getBookTypes());
        return "admin/bookList";
    }

    @GetMapping("/bookListLike")
    public String bookListLike(int pageNum, int pageSize, boolean desc, String query, Model model) {
        ResponseManagerBookListDto responseManagerBookListDto = bookService.getManagerBookList(pageNum, pageSize, desc, query);
        model.addAttribute("pageInfo", responseManagerBookListDto.getPageInfo());
        model.addAttribute("desc", desc);
        return "admin/bookList :: #bookListReplace";
    }

    @GetMapping("/getBook")
    @ResponseBody
    public ResponseBookDto getBook(long id) {
        ResponseBookDto responseBookDto = bookService.getBook(id);
        return responseBookDto;
    }

    @DeleteMapping("/delBook/{id}")
    @ResponseBody
    public ResponseCommonDto delBook(@PathVariable("id") Long id) {
        ResponseCommonDto responseCommonDto = bookService.delBook(id);
        return responseCommonDto;
    }

    /**
     * 视频
     * @return
     */
    @GetMapping("/uploadVideo")
    public String uploadVideo(Model model) {
        ResponseGetVideoTypeDto responseGetVideoTypeDto = videoTypeService.getVideoType();
        model.addAttribute("videoTypes", responseGetVideoTypeDto.getVideoTypes());
        return "admin/uploadVideo";
    }

    @PostMapping("/saveVideo")
    @ResponseBody
    public ResponseCommonDto saveVideo(RequestVideoDto requestVideoDto) {
        ResponseCommonDto responseCommonDto = videoService.saveVideo(requestVideoDto);
        return responseCommonDto;
    }

    @GetMapping("/videoList")
    public String videoList(int pageNum, int pageSize, boolean desc, Model model) {
        ResponseManagerVideoListDto responseManagerVideoListDto = videoService.getManagerVideoList(pageNum, pageSize, desc);
        ResponseGetVideoTypeDto responseGetVideoTypeDto = videoTypeService.getVideoType();
        model.addAttribute("pageInfo", responseManagerVideoListDto.getPageInfo());
        model.addAttribute("videoTypes", responseGetVideoTypeDto.getVideoTypes());
        return "admin/videoList";
    }

    @GetMapping("/videoListLike")
    public String videoListLike(int pageNum, int pageSize, boolean desc, String query, Model model) {
        ResponseManagerVideoListDto responseManagerVideoListDto = videoService.getManagerVideoList(pageNum, pageSize, desc, query);
        model.addAttribute("pageInfo", responseManagerVideoListDto.getPageInfo());
        model.addAttribute("desc", desc);
        return "admin/videoList :: #videoListReplace";
    }

    @GetMapping("/getVideo")
    @ResponseBody
    public ResponseVideoDto getVideo(long id) {
        ResponseVideoDto responseVideoDto = videoService.getVideo(id);
        return responseVideoDto;
    }

    @DeleteMapping("/delVideo/{id}")
    @ResponseBody
    public ResponseCommonDto delVideo(@PathVariable("id") Long id) {
        ResponseCommonDto responseCommonDto = videoService.delVideo(id);
        return responseCommonDto;
    }

    /**
     * 音乐
     * @return
     */
    @GetMapping("/uploadMusic")
    public String uploadMusic(Model model) {
        ResponseGetMusicTypeDto responseGetMusicTypeDto = musicTypeService.getMusicType();
        model.addAttribute("musicTypes", responseGetMusicTypeDto.getMusicTypes());
        return "admin/uploadMusic";
    }

    @PostMapping("/saveMusic")
    @ResponseBody
    public ResponseCommonDto saveMusic(RequestMusicDto requestMusicDto) {
        ResponseCommonDto responseCommonDto = musicService.saveMusic(requestMusicDto);
        return responseCommonDto;
    }

    @GetMapping("/musicList")
    public String musicList(int pageNum, int pageSize, boolean desc, Model model) {
        ResponseManagerMusicListDto responseManagerMusicListDto = musicService.getManagerMusicList(pageNum, pageSize, desc);
        ResponseGetMusicTypeDto responseGetMusicTypeDto = musicTypeService.getMusicType();
        model.addAttribute("pageInfo", responseManagerMusicListDto.getPageInfo());
        model.addAttribute("musicTypes", responseGetMusicTypeDto.getMusicTypes());
        return "admin/musicList";
    }

    @GetMapping("/musicListLike")
    public String musicListLike(int pageNum, int pageSize, boolean desc, String query, Model model) {
        ResponseManagerMusicListDto responseManagerMusicListDto = musicService.getManagerMusicList(pageNum, pageSize, desc, query);
        model.addAttribute("pageInfo", responseManagerMusicListDto.getPageInfo());
        model.addAttribute("desc", desc);
        return "admin/musicList :: #musicListReplace";
    }

    @GetMapping("/getMusic")
    @ResponseBody
    public ResponseMusicDto getMusic(long id) {
        ResponseMusicDto responseMusicDto = musicService.getMusic(id);
        return responseMusicDto;
    }

    @DeleteMapping("/delMusic/{id}")
    @ResponseBody
    public ResponseCommonDto delMusic(@PathVariable("id") Long id) {
        ResponseCommonDto responseCommonDto = musicService.delMusic(id);
        return responseCommonDto;
    }

    /**
     * 图片
     * @return
     */
    @GetMapping("/uploadPicture")
    public String uploadPicture(Model model) {
        ResponseGetPictureTypeDto responseGetPictureTypeDto = pictureTypeService.getPictureType();
        model.addAttribute("pictureTypes", responseGetPictureTypeDto.getPictureTypes());
        return "admin/uploadPicture";
    }

    @PostMapping("/savePicture")
    @ResponseBody
    public ResponseCommonDto savePicture(RequestPictureDto requestPictureDto) {
        ResponseCommonDto responseCommonDto = pictureService.savePicture(requestPictureDto);
        return responseCommonDto;
    }

    @GetMapping("/pictureList")
    public String pictureList(int pageNum, int pageSize, boolean desc, Model model) {
        ResponseManagerPictureListDto responseManagerPictureListDto = pictureService.getManagerPictureList(pageNum, pageSize, desc);
        ResponseGetPictureTypeDto responseGetPictureTypeDto = pictureTypeService.getPictureType();
        model.addAttribute("pageInfo", responseManagerPictureListDto.getPageInfo());
        model.addAttribute("pictureTypes", responseGetPictureTypeDto.getPictureTypes());
        return "admin/pictureList";
    }

    @GetMapping("/pictureListLike")
    public String pictureListLike(int pageNum, int pageSize, boolean desc, String query, Model model) {
        ResponseManagerPictureListDto responseManagerPictureListDto = pictureService.getManagerPictureList(pageNum, pageSize, desc, query);
        model.addAttribute("pageInfo", responseManagerPictureListDto.getPageInfo());
        model.addAttribute("desc", desc);
        return "admin/pictureList :: #pictureListReplace";
    }

    @GetMapping("/getPicture")
    @ResponseBody
    public ResponsePictureDto getPicture(long id) {
        ResponsePictureDto responsePictureDto = pictureService.getPicture(id);
        return responsePictureDto;
    }

    @DeleteMapping("/delPicture/{id}")
    @ResponseBody
    public ResponseCommonDto delPicture(@PathVariable("id") Long id) {
        ResponseCommonDto responseCommonDto = pictureService.delPicture(id);
        return responseCommonDto;
    }

    /**
     * 获取文件服务器参数
     * @param filename
     * @return
     * @throws UnsupportedEncodingException
     */
    @GetMapping("/getBookServer")
    @ResponseBody
    public ResponseUploadDto getBookServer(String filename) throws UnsupportedEncodingException {
        ResponseUploadDto responseUploadDto = ossService.uploadBook(filename);
        return responseUploadDto;
    }

    @GetMapping("/getVideoServer")
    @ResponseBody
    public ResponseUploadDto getVideoServer(String filename) throws UnsupportedEncodingException {
        ResponseUploadDto responseUploadDto = ossService.uploadVideo(filename);
        return responseUploadDto;
    }

    @GetMapping("/getMusicServer")
    @ResponseBody
    public ResponseUploadDto getMusicServer(String filename) throws UnsupportedEncodingException {
        ResponseUploadDto responseUploadDto = ossService.uploadMusic(filename);
        return responseUploadDto;
    }

    @GetMapping("/getPictureServer")
    @ResponseBody
    public ResponseUploadDto getPictureServer(String filename) throws UnsupportedEncodingException {
        ResponseUploadDto responseUploadDto = ossService.uploadPicture(filename);
        return responseUploadDto;
    }

    @GetMapping("/getBookCoverServer")
    @ResponseBody
    public ResponseUploadDto getBookCoverServer(String filename) throws UnsupportedEncodingException {
        ResponseUploadDto responseUploadDto = ossService.uploadBookCover(filename);
        return responseUploadDto;
    }

    @GetMapping("/getVideoCoverServer")
    @ResponseBody
    public ResponseUploadDto getVideoCoverServer(String filename) throws UnsupportedEncodingException {
        ResponseUploadDto responseUploadDto = ossService.uploadVideoCover(filename);
        return responseUploadDto;
    }

    @GetMapping("/getMusicCoverServer")
    @ResponseBody
    public ResponseUploadDto getMusicCoverServer(String filename) throws UnsupportedEncodingException {
        ResponseUploadDto responseUploadDto = ossService.uploadMusicCover(filename);
        return responseUploadDto;
    }

    @GetMapping("/main")
    public String main(Model model) {
        ResponseHotWordsList hotWords = hotWordsService.getHotWords(null, null, null);
        model.addAttribute("pageInfo", hotWords.getPageInfo());
        return "admin/main";
    }

    @GetMapping("/hotWords")
    public String hotWords(Integer pageNum, Integer pageSize, Boolean desc, Model model) {
        ResponseHotWordsList hotWords = hotWordsService.getHotWords(pageNum, pageSize, desc);
        model.addAttribute("pageInfo", hotWords.getPageInfo());
        return "admin/main :: #hotWordsListReplace";
    }

    @GetMapping("/getPageView")
    @ResponseBody
    public ResponsePageViewList getPageView() {
        ResponsePageViewList responsePageViewList = pageViewService.getPageView();
        return responsePageViewList;
    }

    @GetMapping("/initEs")
    @ResponseBody
    public String initEs(){
        boolean init = initEs.init();
        return init ? "Es初始化成功" : "Es初始化失败";
    }
}

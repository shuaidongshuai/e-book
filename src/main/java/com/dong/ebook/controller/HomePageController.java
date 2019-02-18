package com.dong.ebook.controller;

import com.dong.ebook.dto.RequestUserDto;
import com.dong.ebook.model.User;
import com.dong.ebook.security.AuthUserService;
import com.dong.ebook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class HomePageController {
    @Autowired
    UserService userService;

    @Autowired
    AuthUserService authUserService;

    @RequestMapping("/")
    public String index(Model model) {
        User curUser = authUserService.getCurUser();
        model.addAttribute("user", curUser);
        return "/index";
    }

    @GetMapping("/register")
    public String registerUser() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(RequestUserDto RequestUserDto) {
        userService.addUser(RequestUserDto);
        return "redirect:/";
    }

    @RequestMapping("/login")
    public String login() {
        return "/login";
    }

    @GetMapping("/search")
    public String search(){
        return "/search/main";
    }


}

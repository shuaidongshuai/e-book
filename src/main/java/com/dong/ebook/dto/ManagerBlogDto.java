package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagerBlogDto {
    private Long blogId;
    private String blogTitle;
    private String userNickname;
    private String userAvatar;
}

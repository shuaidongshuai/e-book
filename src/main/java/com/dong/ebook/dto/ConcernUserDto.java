package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ConcernUserDto {
    private Long userId;
    private String userAvatar;
    private String userNickname;
    private String userEmail;
    private Date createTime;
}

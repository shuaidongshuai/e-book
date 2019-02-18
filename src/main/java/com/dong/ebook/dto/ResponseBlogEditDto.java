package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseBlogEditDto extends ResponseCommonDto{
    private Long blogId;
    private String title;
    private String content;
    private Long userId;
    private String nickName;
    private String avatar;
}

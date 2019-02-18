package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BlogDto {
    private Long id;
    private Long userId;
    private String title;
    private String summary;
    private Long blogTypeId;
    private String content;
    private String contentHtml;
    private Date createTime;
    private Date modifyTime;
    private Integer traffic;
    private Integer voteNum;
    private Integer commentNum;
}

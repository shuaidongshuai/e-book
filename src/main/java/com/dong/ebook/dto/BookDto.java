package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BookDto {
    private Long id;
    private String fileUrl;
    private String coverUrl;
    private String name;
    private String catalog;
    private String introduction;
    private Long bookTypeId;
    private Long userId;
    private Date createTime;
    private Date modifyTime;
}

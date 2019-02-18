package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class MusicDto {
    private Long id;
    private String fileUrl;
    private String coverUrl;
    private String name;
    private String author;
    private String composer;
    private String singer;
    private Long musicTypeId;
    private Long modifyUserId;
    private Date createTime;
    private Date modifyTime;
}

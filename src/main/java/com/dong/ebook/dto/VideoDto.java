package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class VideoDto {
    private Long id;
    private String fileUrl;
    private String coverUrl;
    private String title;
    private Long videoTypeId;
    private Long modifyUserId;
    private Date createTime;
    private Date modifyTime;
}

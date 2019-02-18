package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PictureDto {
    private Long id;
    private String title;
    private Long pictureTypeId;
    private Date createTime;
    private Date modifyTime;
    private String urlJson;
}

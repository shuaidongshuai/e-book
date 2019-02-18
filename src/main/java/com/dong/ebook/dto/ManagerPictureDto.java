package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ManagerPictureDto {
    private Long id;
    private String fileUrl;
    private String coverUrl;
    private Long pictureTypeId;
    private String title;
    private Date modifyTime;
    private String modifyUserNickname;
}

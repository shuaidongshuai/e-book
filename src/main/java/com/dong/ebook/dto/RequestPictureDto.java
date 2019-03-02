package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestPictureDto {
    private Long id;
    private String title;
    private String urlJson;
    private Long pictureTypeId;
}

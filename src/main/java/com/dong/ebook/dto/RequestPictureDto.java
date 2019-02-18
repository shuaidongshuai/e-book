package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestPictureDto {
    private Long id;
    private String title;
    private List<String> urls;
    private String urlJson;
    private Long pictureTypeId;
}

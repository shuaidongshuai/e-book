package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseMainPagePictureListDto extends ResponseCommonDto{
    private List<PictureDto> bigPictureDtos;
    private List<PictureDto> smallPictureDtos;
    private List<PictureDto> circlePictureDtos;
}

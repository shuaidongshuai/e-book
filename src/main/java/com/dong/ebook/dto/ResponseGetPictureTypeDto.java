package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseGetPictureTypeDto extends ResponseCommonDto {
    private List<PictureTypeDto> pictureTypes;
}

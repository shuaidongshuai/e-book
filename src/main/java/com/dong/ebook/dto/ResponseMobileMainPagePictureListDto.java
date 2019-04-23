package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseMobileMainPagePictureListDto extends ResponseCommonDto{
    private List<PictureDto> pictures;
}

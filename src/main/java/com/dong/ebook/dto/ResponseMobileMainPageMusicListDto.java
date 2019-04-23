package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseMobileMainPageMusicListDto extends ResponseCommonDto{
    private List<MusicDto> musics;
}

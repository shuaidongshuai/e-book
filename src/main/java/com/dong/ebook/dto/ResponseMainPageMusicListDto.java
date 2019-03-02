package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseMainPageMusicListDto extends ResponseCommonDto{
    private List<MusicDto> firstPageMusic;
    private List<MusicDto> secondPageMusic;
    private List<MusicDto> thirdPageMusic;
    private List<MusicDto> popularMusic;
    private List<MusicDto> englishMusic;
    private List<MusicDto> douyinMusic;
}

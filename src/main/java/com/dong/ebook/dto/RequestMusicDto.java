package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestMusicDto {
    private Long id;
    private String fileUrl;
    private String coverUrl;
    private String name;
    private String author;
    private String composer;
    private String singer;
    private Long musicTypeId;
}

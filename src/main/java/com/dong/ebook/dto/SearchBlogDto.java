package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchBlogDto {
    private ElasticsearchBlogDto blogDto;
    private String userNickname;
    private String userAvatar;
}

package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestVideoDto {
    private Long id;
    private String fileUrl;
    private String coverUrl;
    private String title;
    private Long videoTypeId;
}

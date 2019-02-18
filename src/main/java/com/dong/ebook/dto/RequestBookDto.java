package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestBookDto {
    private Long id;
    private String fileUrl;
    private String coverUrl;
    private String name;
    private String catalog;
    private String introduction;
    private Long bookTypeId;
}

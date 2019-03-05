package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PageViewDto {
    private Long number;
    private Date createTime;
}

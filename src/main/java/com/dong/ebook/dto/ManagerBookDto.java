package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ManagerBookDto {
    private Long id;
    private String fileUrl;
    private String coverUrl;
    private String name;
    private Date modifyTime;
    private String modifyUserNickname;
}

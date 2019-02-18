package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreferenceDto {
    private Long typeId;
    private String className;
    private Boolean checked;
}

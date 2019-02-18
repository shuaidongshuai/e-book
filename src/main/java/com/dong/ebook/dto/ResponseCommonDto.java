package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseCommonDto {
    private boolean success;
    private String errorCode;
    private String errorMsg;
}

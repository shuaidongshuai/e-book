package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseUserDto extends ResponseCommonDto {
    private UserDto user;
}

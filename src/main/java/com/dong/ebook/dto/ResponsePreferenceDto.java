package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponsePreferenceDto extends ResponseCommonDto {
    List<PreferenceDto> preferenceDtos;
}

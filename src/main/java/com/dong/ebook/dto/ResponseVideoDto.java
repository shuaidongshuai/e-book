package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseVideoDto extends ResponseCommonDto{
    private VideoDto videoDto;
}

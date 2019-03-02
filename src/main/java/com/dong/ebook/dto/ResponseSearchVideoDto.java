package com.dong.ebook.dto;

import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseSearchVideoDto extends ResponseCommonDto {
    private PageInfo<ElasticsearchVideoDto> pageInfo;
}

package com.dong.ebook.dto;

import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseSearchPictureDto extends ResponseCommonDto {
    private PageInfo<ElasticsearchPictureDto> pageInfo;
}

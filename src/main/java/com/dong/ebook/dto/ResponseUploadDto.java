package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseUploadDto extends ResponseCommonDto {
    private String host;
    private String accessKeyId;
    private String policy;
    private String signature;
    private String filename;
    private String fileUrl;
}

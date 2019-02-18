package com.dong.ebook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseBlogPageDto extends ResponseCommonDto{
    private BlogDto blogDto;
    private String nickname;
    private String avatar;
    private Integer articleNum;
    private Integer fansNum;
    private Integer voteNum;
    private Integer commentNum;
    private Boolean selfBlog;
    private Boolean concern;
    private Boolean vote;
}

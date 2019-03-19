package com.dong.ebook.dto;

import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseUserBlogListDto extends ResponseCommonDto{
    private PageInfo<BlogDto> pageInfo;
    private Long userId;
    private String nickname;
    private String avatar;
    private Integer articleNum;
    private Integer fansNum;
    private Integer voteNum;
    private Integer commentNum;
    private Boolean selfBlog;
    private Boolean concern;
}

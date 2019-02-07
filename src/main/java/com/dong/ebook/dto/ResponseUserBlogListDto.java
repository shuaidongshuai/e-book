package com.dong.ebook.dto;

import com.github.pagehelper.PageInfo;

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

    public PageInfo<BlogDto> getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo<BlogDto> pageInfo) {
        this.pageInfo = pageInfo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getArticleNum() {
        return articleNum;
    }

    public void setArticleNum(Integer articleNum) {
        this.articleNum = articleNum;
    }

    public Integer getFansNum() {
        return fansNum;
    }

    public void setFansNum(Integer fansNum) {
        this.fansNum = fansNum;
    }

    public Integer getVoteNum() {
        return voteNum;
    }

    public void setVoteNum(Integer voteNum) {
        this.voteNum = voteNum;
    }

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }

    public Boolean getSelfBlog() {
        return selfBlog;
    }

    public void setSelfBlog(Boolean selfBlog) {
        this.selfBlog = selfBlog;
    }
}

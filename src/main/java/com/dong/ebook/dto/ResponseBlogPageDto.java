package com.dong.ebook.dto;

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

    public BlogDto getBlogDto() {
        return blogDto;
    }

    public void setBlogDto(BlogDto blogDto) {
        this.blogDto = blogDto;
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

    public Boolean getConcern() {
        return concern;
    }

    public void setConcern(Boolean concern) {
        this.concern = concern;
    }

    public Boolean getVote() {
        return vote;
    }

    public void setVote(Boolean vote) {
        this.vote = vote;
    }
}

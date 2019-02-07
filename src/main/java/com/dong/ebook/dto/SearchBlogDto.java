package com.dong.ebook.dto;

public class SearchBlogDto {
    private ElasticsearchBlogDto blogDto;
    private String userNickname;
    private String userAvatar;

    public ElasticsearchBlogDto getBlogDto() {
        return blogDto;
    }

    public void setBlogDto(ElasticsearchBlogDto blogDto) {
        this.blogDto = blogDto;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }
}

package com.dong.ebook.service;

import com.dong.ebook.dto.*;

public interface ElasticsearchService {
    /**
     * blog
     */
    void addBlog(ElasticsearchBlogDto elasticsearchBlogDto);
    void delBlog(Long id);
    void updateBlog(ElasticsearchBlogDto elasticsearchBlogDto);
    ElasticsearchBlogDto getBlog(Long id);
    ResponseSearchBlogDto getBlog(String query, Integer pageNum, Integer pageSize);

    /**
     * book
     */
    void addBook(ElasticsearchBookDto elasticsearchBookDto);
    void delBook(Long id);
    void updateBook(ElasticsearchBookDto elasticsearchBookDto);
    ElasticsearchBookDto getBook(Long id);
    ResponseSearchBookDto getBook(String query, Integer pageNum, Integer pageSize);

    /**
     * video
     */
    void addVideo(ElasticsearchVideoDto elasticsearchVideoDto);
    void delVideo(Long id);
    void updateVideo(ElasticsearchVideoDto elasticsearchVideoDto);
    ElasticsearchVideoDto getVideo(Long id);
    ResponseSearchVideoDto getVideo(String query, Integer pageNum, Integer pageSize);

    /**
     * music
     */
    void addMusic(ElasticsearchMusicDto elasticsearchMusicDto);
    void delMusic(Long id);
    void updateMusic(ElasticsearchMusicDto elasticsearchMusicDto);
    ElasticsearchMusicDto getMusic(Long id);
    ResponseSearchMusicDto getMusic(String query, Integer pageNum, Integer pageSize);

    /**
     * picture
     */
    void addPicture(ElasticsearchPictureDto elasticsearchPictureDto);
    void delPicture(Long id);
    void updatePicture(ElasticsearchPictureDto elasticsearchPictureDto);
    ElasticsearchPictureDto getPicture(Long id);
    ResponseSearchPictureDto getPicture(String query, Integer pageNum, Integer pageSize);
}

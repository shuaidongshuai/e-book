package com.dong.ebook.service;

import com.dong.ebook.dto.ResponseUploadDto;

import java.io.UnsupportedEncodingException;

public interface OssService {
    ResponseUploadDto uploadAvatar() throws UnsupportedEncodingException;

    ResponseUploadDto uploadBlogImage() throws UnsupportedEncodingException;

    ResponseUploadDto uploadBook(String filename) throws UnsupportedEncodingException;

    ResponseUploadDto uploadVideo(String filename) throws UnsupportedEncodingException;

    ResponseUploadDto uploadMusic(String filename) throws UnsupportedEncodingException;

    ResponseUploadDto uploadPicture(String filename) throws UnsupportedEncodingException;

    ResponseUploadDto uploadBookCover(String filename) throws UnsupportedEncodingException;

    ResponseUploadDto uploadVideoCover(String filename) throws UnsupportedEncodingException;

    ResponseUploadDto uploadMusicCover(String filename) throws UnsupportedEncodingException;

}

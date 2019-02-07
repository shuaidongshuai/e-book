package com.dong.ebook.service;

import com.dong.ebook.dto.ResponseUploadDto;
import com.dong.ebook.model.User;

import java.io.UnsupportedEncodingException;

public interface OssService {
    ResponseUploadDto uploadAvatar(User user) throws UnsupportedEncodingException;

    ResponseUploadDto uploadBlogImage(User user) throws UnsupportedEncodingException;

    ResponseUploadDto uploadBook(String filename) throws UnsupportedEncodingException;

    ResponseUploadDto uploadVideo(String filename) throws UnsupportedEncodingException;

    ResponseUploadDto uploadMusic(String filename) throws UnsupportedEncodingException;

    ResponseUploadDto uploadPicture(String filename) throws UnsupportedEncodingException;

    ResponseUploadDto uploadFile(String type, String filename) throws UnsupportedEncodingException;
}

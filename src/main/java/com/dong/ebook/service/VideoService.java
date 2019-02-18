package com.dong.ebook.service;

import com.dong.ebook.dto.RequestVideoDto;
import com.dong.ebook.dto.ResponseCommonDto;
import com.dong.ebook.dto.ResponseManagerVideoListDto;
import com.dong.ebook.dto.ResponseVideoDto;

public interface VideoService {
    ResponseCommonDto saveVideo(RequestVideoDto requestVideoDto);
    ResponseCommonDto delVideo(long id);
    ResponseManagerVideoListDto getManagerVideoList(int pageNum, int pageSize, boolean desc);
    ResponseManagerVideoListDto getManagerVideoList(int pageNum, int pageSize, boolean desc, String query);
    ResponseVideoDto getVideo(long id);
}

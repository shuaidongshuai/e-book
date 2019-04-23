package com.dong.ebook.service;

import com.dong.ebook.dto.*;

public interface VideoService {
    ResponseCommonDto saveVideo(RequestVideoDto requestVideoDto);
    ResponseCommonDto delVideo(long id);
    ResponseManagerVideoListDto getManagerVideoList(int pageNum, int pageSize, boolean desc);
    ResponseManagerVideoListDto getManagerVideoList(int pageNum, int pageSize, boolean desc, String query);
    ResponseVideoDto getVideo(long id);

    /**
     * 为主页定制的List
     * @return
     */
    ResponseMainPageVideoListDto getMainPageVideoList();
    ResponseMobileMainPageVideoListDto getMobileMainPageVideoList();
}

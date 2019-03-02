package com.dong.ebook.service;

import com.dong.ebook.dto.*;

public interface VideoService {
    ResponseCommonDto saveVideo(RequestVideoDto requestVideoDto);
    ResponseCommonDto delVideo(long id);
    ResponseManagerVideoListDto getManagerVideoList(int pageNum, int pageSize, boolean desc);
    ResponseManagerVideoListDto getManagerVideoList(int pageNum, int pageSize, boolean desc, String query);
    ResponseVideoDto getVideo(long id);

    /**
     * 根据用户兴趣获取列表
     * @return
     */
    ResponseMainPageVideoListDto getMainPageVideoList();
}

package com.dong.ebook.service;

import com.dong.ebook.dto.*;

public interface MusicService {
    ResponseCommonDto saveMusic(RequestMusicDto requestMusicDto);
    ResponseCommonDto delMusic(long id);
    ResponseManagerMusicListDto getManagerMusicList(int pageNum, int pageSize, boolean desc);
    ResponseManagerMusicListDto getManagerMusicList(int pageNum, int pageSize, boolean desc, String query);
    ResponseMusicDto getMusic(long id);

    /**
     * 为主页定制的List
     * @return
     */
    ResponseMainPageMusicListDto getMainPageMusicList();
    ResponseMobileMainPageMusicListDto getMobileMainPageMusicList();
}

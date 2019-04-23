package com.dong.ebook.service;

import com.dong.ebook.dto.*;

public interface PictureService {
    ResponseCommonDto savePicture(RequestPictureDto requestPictureDto);
    ResponseManagerPictureListDto getManagerPictureList(int pageNum, int pageSize, boolean desc);
    ResponseManagerPictureListDto getManagerPictureList(int pageNum, int pageSize, boolean desc, String query);
    ResponsePictureDto getPicture(long id);
    ResponseCommonDto delPicture(Long id);

    /**
     * 为主页定制的List
     * @return
     */
    ResponseMainPagePictureListDto getMainPagePictureList();
    ResponseMobileMainPagePictureListDto getMobileMainPagePictureList();
}

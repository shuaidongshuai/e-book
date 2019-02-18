package com.dong.ebook.service;

import com.dong.ebook.dto.ResponseCommonDto;
import com.dong.ebook.dto.ResponsePreferenceDto;

public interface PreferenceService {
    ResponsePreferenceDto getBookPreference();
    ResponseCommonDto addBookPreference(long typeId);
    ResponseCommonDto delBookPreference(long typeId);
    ResponsePreferenceDto getVideoPreference();
    ResponseCommonDto addVideoPreference(long typeId);
    ResponseCommonDto delVideoPreference(long typeId);
    ResponsePreferenceDto getMusicPreference();
    ResponseCommonDto addMusicPreference(long typeId);
    ResponseCommonDto delMusicPreference(long typeId);
    ResponsePreferenceDto getPicturePreference();
    ResponseCommonDto addPicturePreference(long typeId);
    ResponseCommonDto delPicturePreference(long typeId);
    ResponsePreferenceDto getBlogPreference();
    ResponseCommonDto addBlogPreference(long typeId);
    ResponseCommonDto delBlogPreference(long typeId);
}


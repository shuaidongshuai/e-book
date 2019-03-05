package com.dong.ebook.service;

import com.dong.ebook.dto.ResponseHotWordsList;

public interface HotWordsService {
    ResponseHotWordsList getHotWords(Integer pageNum, Integer pageSize, Boolean desc);
}

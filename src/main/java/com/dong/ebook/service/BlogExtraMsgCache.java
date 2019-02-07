package com.dong.ebook.service;

import java.util.List;

public interface BlogExtraMsgCache<KEY, VALUE> {
    Integer getArticleNum(KEY key);
    Integer getFansNum(KEY key);
    Integer getVoteNum(KEY key);
    Integer getCommentNum(KEY key);
    List<VALUE> getAll(KEY key);

    void addArticleNum(KEY key, VALUE value);
    void addFansNum(KEY key, VALUE value);
    void addVoteNum(KEY key, VALUE value);
    void addCommentNum(KEY key, VALUE value);
    void addAll(KEY key, List<VALUE> values);
}

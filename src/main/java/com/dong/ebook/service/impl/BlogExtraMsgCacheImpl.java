package com.dong.ebook.service.impl;

import com.dong.ebook.service.BlogExtraMsgCache;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@Service
public class BlogExtraMsgCacheImpl implements BlogExtraMsgCache<Long, Integer> {
    //缓存 userId -> 用户文章数, 粉丝数，点赞数，评论数
    private Hashtable<Long, List<Integer>> userBlogExtraMsg = new Hashtable<>(64);
    private int valueSize = 4;

    @Override
    public Integer getArticleNum(Long key) {
        return userBlogExtraMsg.get(key).get(0);
    }

    @Override
    public Integer getFansNum(Long key) {
        return userBlogExtraMsg.get(key).get(1);
    }

    @Override
    public Integer getVoteNum(Long key) {
        return userBlogExtraMsg.get(key).get(2);
    }

    @Override
    public Integer getCommentNum(Long key) {
        return userBlogExtraMsg.get(key).get(3);
    }

    @Override
    public List<Integer> getAll(Long key) {
        return userBlogExtraMsg.get(key);
    }

    @Override
    public void addArticleNum(Long key, Integer value) {
        List<Integer> values = getValues(key);
        values.set(0, values.get(0) + value);
    }

    @Override
    public void addFansNum(Long key, Integer value) {
        List<Integer> values = getValues(key);
        values.set(1, values.get(1) + value);
    }

    @Override
    public void addVoteNum(Long key, Integer value) {
        List<Integer> values = getValues(key);
        values.set(2, values.get(2) + value);
    }

    @Override
    public void addCommentNum(Long key, Integer value) {
        List<Integer> values = getValues(key);
        values.set(3, values.get(3) + value);
    }

    @Override
    public void addAll(Long key, List<Integer> values) {
        List<Integer> _values = getValues(key);
        _values.set(0, _values.get(0) + values.get(0));
        _values.set(1, _values.get(1) + values.get(1));
        _values.set(2, _values.get(2) + values.get(2));
        _values.set(3, _values.get(3) + values.get(3));
    }

    public List<Integer> getValues(Long key){
        List<Integer> values = userBlogExtraMsg.get(key);
        if(values == null){
            values = new ArrayList<>(valueSize);
            for(int i = 0; i < valueSize; ++i){
                values.add(0);
            }
        }
        return values;
    }
}

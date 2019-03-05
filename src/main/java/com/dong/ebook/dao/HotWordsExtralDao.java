package com.dong.ebook.dao;

import com.dong.ebook.model.HotWords;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HotWordsExtralDao {
    @Insert({
            "<script>",
            "insert into hot_words (word, number, create_time, modify_time) values ",
            "<foreach item='hotWords' index='idx' collection='hotWordsList' separator=','>",
            "(#{hotWords.word}, #{hotWords.number}, #{hotWords.createTime}, #{hotWords.modifyTime})",
            "</foreach>",
            "</script>"
    })
    void insertList(@Param(value="hotWordsList") List<HotWords> hotWordsList);

    @Delete("DELETE FROM hot_words")
    void deleteAll();
}
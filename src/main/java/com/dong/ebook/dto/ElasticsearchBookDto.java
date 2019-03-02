package com.dong.ebook.dto;

import com.dong.ebook.model.BookWithBLOBs;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

/**
 * indexName索引名称 可以理解为数据库名 必须为小写 不然会报org.elasticsearch.indices.InvalidIndexNameException异常
 * type类型 可以理解为表名
 */
@Document(indexName = "book", type = "book")
public class ElasticsearchBookDto extends BookWithBLOBs implements Serializable {
}

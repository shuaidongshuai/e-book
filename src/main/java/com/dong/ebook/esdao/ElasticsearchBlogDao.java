package com.dong.ebook.esdao;

import com.dong.ebook.dto.ElasticsearchBlogDto;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

@Component
public interface ElasticsearchBlogDao extends ElasticsearchRepository<ElasticsearchBlogDto, Long> {
}

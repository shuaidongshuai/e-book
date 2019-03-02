package com.dong.ebook.esdao;

import com.dong.ebook.dto.ElasticsearchVideoDto;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

@Component
public interface ElasticsearchVideoDao extends ElasticsearchRepository<ElasticsearchVideoDto, Long> {
}

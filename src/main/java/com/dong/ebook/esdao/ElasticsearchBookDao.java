package com.dong.ebook.esdao;

import com.dong.ebook.dto.ElasticsearchBookDto;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

@Component
public interface ElasticsearchBookDao extends ElasticsearchRepository<ElasticsearchBookDto, Long> {
}

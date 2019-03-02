package com.dong.ebook.esdao;

import com.dong.ebook.dto.ElasticsearchPictureDto;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

@Component
public interface ElasticsearchPictureDao extends ElasticsearchRepository<ElasticsearchPictureDto, Long> {
}

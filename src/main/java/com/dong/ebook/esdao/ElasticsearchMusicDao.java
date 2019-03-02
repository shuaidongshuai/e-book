package com.dong.ebook.esdao;

import com.dong.ebook.dto.ElasticsearchMusicDto;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

@Component
public interface ElasticsearchMusicDao extends ElasticsearchRepository<ElasticsearchMusicDto, Long> {
}

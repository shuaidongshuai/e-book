package com.dong.ebook.service.impl;

import com.dong.ebook.dto.*;
import com.dong.ebook.esdao.ElasticsearchBlogDao;
import com.dong.ebook.service.ElasticsearchService;
import com.dong.ebook.service.UserService;
import com.github.pagehelper.PageInfo;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {
    @Autowired
    ElasticsearchBlogDao elasticsearchBlogDao;

    @Autowired
    UserService userService;

    @Override
    public void addBlog(ElasticsearchBlogDto elasticsearchBlogDto) {
        elasticsearchBlogDao.save(elasticsearchBlogDto);
    }

    @Override
    public void delBlog(Long id) {
        elasticsearchBlogDao.deleteById(id);
    }

    @Override
    public void updateBlog(ElasticsearchBlogDto elasticsearchBlogDto) {
        elasticsearchBlogDao.save(elasticsearchBlogDto);
    }

    @Override
    public ElasticsearchBlogDto getBlog(Long id) {
        Optional<ElasticsearchBlogDto> optional = elasticsearchBlogDao.findById(id);
        return optional.get();
    }

    @Override
    public ResponseSearchBlogDto getBlog(String query, Integer pageNum, Integer pageSize) {
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(query, "title", "content");
//        PageRequest pageRequest = new PageRequest(pageNum - 1, pageSize, new Sort(new Sort.Order(QSort.Direction.DESC, "voteNum")));
//        Page<ElasticsearchBlogDto> search = elasticsearchBlogDao.search(multiMatchQueryBuilder, pageRequest);

        FunctionScoreQueryBuilder.FilterFunctionBuilder[] functionBuilders = {
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchPhraseQuery("title", query), ScoreFunctionBuilders.weightFactorFunction(70)),
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchPhraseQuery("content", query), ScoreFunctionBuilders.weightFactorFunction(30))};
        PageRequest pageRequest = new PageRequest(pageNum - 1, pageSize);
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(QueryBuilders.multiMatchQuery(query, "title", "content"), functionBuilders);
        Page<ElasticsearchBlogDto> search = elasticsearchBlogDao.search(functionScoreQueryBuilder, pageRequest);

        //切分摘要
        List<ElasticsearchBlogDto> elasticsearchBlogDtos = search.getContent();
        summary130(elasticsearchBlogDtos);

        //查询每篇blog的用户信息
        List<SearchBlogDto> searchBlogDtos = assembleSearchBlogDtos(elasticsearchBlogDtos);

        //组装结果
        PageInfo pageInfo = assemblePageInfo(searchBlogDtos, pageNum, pageSize, search.getTotalElements(), search.getTotalPages());

        ResponseSearchBlogDto responseSearchBlogDto = new ResponseSearchBlogDto();
        responseSearchBlogDto.setPageInfo(pageInfo);
        responseSearchBlogDto.setSuccess(true);
        return responseSearchBlogDto;
    }

    /**
     * 控制摘要长度为130
     * @param list
     * @return
     */
    public void summary130(List<ElasticsearchBlogDto> list){
        String summary;
        for(ElasticsearchBlogDto elasticsearchBlogDto : list){
            summary = elasticsearchBlogDto.getSummary();
            if(summary.length() > 130){
                elasticsearchBlogDto.setSummary(summary.substring(0, 130));
            }
        }
    }

    /**
     * 组装用户信息
     * @param elasticsearchBlogDtos
     * @return
     */
    public List<SearchBlogDto> assembleSearchBlogDtos(List<ElasticsearchBlogDto> elasticsearchBlogDtos){
        List<SearchBlogDto> list = new ArrayList<>();
        for(ElasticsearchBlogDto elasticsearchBlogDto : elasticsearchBlogDtos){
            UserDto user = userService.findById(elasticsearchBlogDto.getUserId()).getUser();

            SearchBlogDto searchBlogDto = new SearchBlogDto();
            list.add(searchBlogDto);

            searchBlogDto.setBlogDto(elasticsearchBlogDto);
            searchBlogDto.setUserNickname(user.getNickname());
            searchBlogDto.setUserAvatar(user.getAvatar());
        }
        return list;
    }

    /**
     * 组装pageInfo
     * @param searchBlogDtos
     * @param pageNum
     * @param pageSize
     * @param total
     * @param pages
     * @return
     */
    public PageInfo assemblePageInfo(List<SearchBlogDto> searchBlogDtos, Integer pageNum, Integer pageSize, Long total, Integer pages){
        PageInfo pageInfo = new PageInfo(searchBlogDtos);
        pageInfo.setTotal(total);
        pageInfo.setPages(pages);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setHasPreviousPage(pageNum.equals(1) ? false : true);
        pageInfo.setHasNextPage(pageNum.equals(pages) ? false : true);
        return pageInfo;
    }
}

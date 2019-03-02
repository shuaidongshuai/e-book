package com.dong.ebook.service.impl;

import com.dong.ebook.dto.*;
import com.dong.ebook.esdao.*;
import com.dong.ebook.model.User;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {
    @Autowired
    private ElasticsearchBlogDao elasticsearchBlogDao;

    @Autowired
    private ElasticsearchBookDao elasticsearchBookDao;

    @Autowired
    private ElasticsearchVideoDao elasticsearchVideoDao;

    @Autowired
    private ElasticsearchMusicDao elasticsearchMusicDao;

    @Autowired
    private ElasticsearchPictureDao elasticsearchPictureDao;

    @Autowired
    private UserService userService;

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
        Optional<ElasticsearchBlogDto> optional = elasticsearchBlogDao.findById(elasticsearchBlogDto.getId());
        if(optional != null){
            ElasticsearchBlogDto blog = optional.get();
            if(elasticsearchBlogDto.getTitle() == null){
                elasticsearchBlogDto.setTitle(blog.getTitle());
            }
            if(elasticsearchBlogDto.getSummary() == null){
                elasticsearchBlogDto.setSummary(blog.getSummary());
            }
            if(elasticsearchBlogDto.getContent() == null){
                elasticsearchBlogDto.setContent(blog.getContent());
            }
            if(elasticsearchBlogDto.getContentHtml() == null){
                elasticsearchBlogDto.setContentHtml(blog.getContentHtml());
            }
            if(elasticsearchBlogDto.getBlogTypeId() == null){
                elasticsearchBlogDto.setBlogTypeId(blog.getBlogTypeId());
            }
            if(elasticsearchBlogDto.getTraffic() == null){
                elasticsearchBlogDto.setTraffic(blog.getTraffic());
            }
            if(elasticsearchBlogDto.getVoteNum() == null){
                elasticsearchBlogDto.setVoteNum(blog.getVoteNum());
            }
            if(elasticsearchBlogDto.getCommentNum() == null){
                elasticsearchBlogDto.setCommentNum(blog.getCommentNum());
            }
            if(elasticsearchBlogDto.getCreateTime() == null){
                elasticsearchBlogDto.setCreateTime(blog.getCreateTime());
            }
            if(elasticsearchBlogDto.getModifyTime() == null){
                elasticsearchBlogDto.setModifyTime(blog.getModifyTime());
            }
        }
        elasticsearchBlogDao.save(elasticsearchBlogDto);
    }

    @Override
    public ElasticsearchBlogDto getBlog(Long id) {
        Optional<ElasticsearchBlogDto> optional = elasticsearchBlogDao.findById(id);
        return optional.get();
    }

    @Override
    public ResponseSearchBlogDto getBlog(String query, Integer pageNum, Integer pageSize) {
//        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(query, "title", "content");
//        PageRequest pageRequest = new PageRequest(pageNum - 1, pageSize, new Sort(new Sort.Order(QSort.Direction.DESC, "voteNum")));
//        Page<ElasticsearchBlogDto> search = elasticsearchBlogDao.search(multiMatchQueryBuilder, pageRequest);

        if(pageNum == null || pageSize == null){
            pageNum = 1;
            pageSize = 5;
        }
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

    @Override
    public void addBook(ElasticsearchBookDto elasticsearchBookDto) {
        elasticsearchBookDao.save(elasticsearchBookDto);
    }

    @Override
    public void delBook(Long id) {
        elasticsearchBookDao.deleteById(id);
    }

    @Override
    public void updateBook(ElasticsearchBookDto elasticsearchBookDto) {
        Optional<ElasticsearchBookDto> optional = elasticsearchBookDao.findById(elasticsearchBookDto.getId());
        if(optional != null){
            ElasticsearchBookDto book = optional.get();
            if(elasticsearchBookDto.getFileUrl() == null){
                elasticsearchBookDto.setFileUrl(book.getFileUrl());
            }
            if(elasticsearchBookDto.getCoverUrl() == null){
                elasticsearchBookDto.setCoverUrl(book.getCoverUrl());
            }
            if(elasticsearchBookDto.getName() == null){
                elasticsearchBookDto.setName(book.getName());
            }
            if(elasticsearchBookDto.getCatalog() == null){
                elasticsearchBookDto.setCatalog(book.getCatalog());
            }
            if(elasticsearchBookDto.getIntroduction() == null){
                elasticsearchBookDto.setIntroduction(book.getIntroduction());
            }
            if(elasticsearchBookDto.getBookTypeId() == null){
                elasticsearchBookDto.setBookTypeId(book.getBookTypeId());
            }
            if(elasticsearchBookDto.getModifyUserId() == null){
                elasticsearchBookDto.setModifyUserId(book.getModifyUserId());
            }
            if(elasticsearchBookDto.getCreateTime() == null){
                elasticsearchBookDto.setCreateTime(book.getCreateTime());
            }
            if(elasticsearchBookDto.getModifyTime() == null){
                elasticsearchBookDto.setModifyTime(book.getModifyTime());
            }

        }
        elasticsearchBookDao.save(elasticsearchBookDto);
    }

    @Override
    public ElasticsearchBookDto getBook(Long id) {
        Optional<ElasticsearchBookDto> optional = elasticsearchBookDao.findById(id);
        return optional.get();
    }

    @Override
    public ResponseSearchBookDto getBook(String query, Integer pageNum, Integer pageSize) {
        if(pageNum == null || pageSize == null){
            pageNum = 1;
            pageSize = 10;
        }

        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(query, "name");
        Sort sort = new Sort(Sort.Direction.DESC,"modifyTime");
        PageRequest pageRequest = new PageRequest(pageNum - 1, pageSize, sort);
        Page<ElasticsearchBookDto> search = elasticsearchBookDao.search(multiMatchQueryBuilder, pageRequest);
        List<ElasticsearchBookDto> elasticsearchBookDtos = search.getContent();

        assembleBookDtos(elasticsearchBookDtos);

        //组装结果
        PageInfo pageInfo = assemblePageInfo(elasticsearchBookDtos, pageNum, pageSize, search.getTotalElements(), search.getTotalPages());

        ResponseSearchBookDto responseSearchBookDto = new ResponseSearchBookDto();
        responseSearchBookDto.setPageInfo(pageInfo);
        responseSearchBookDto.setSuccess(true);
        return responseSearchBookDto;
    }

    @Override
    public void addVideo(ElasticsearchVideoDto elasticsearchVideoDto) {
        elasticsearchVideoDao.save(elasticsearchVideoDto);
    }

    @Override
    public void delVideo(Long id) {
        elasticsearchVideoDao.deleteById(id);
    }

    @Override
    public void updateVideo(ElasticsearchVideoDto elasticsearchVideoDto) {
        Optional<ElasticsearchVideoDto> optional = elasticsearchVideoDao.findById(elasticsearchVideoDto.getId());
        if(optional != null){
            ElasticsearchVideoDto book = optional.get();
            if(elasticsearchVideoDto.getFileUrl() == null){
                elasticsearchVideoDto.setFileUrl(book.getFileUrl());
            }
            if(elasticsearchVideoDto.getCoverUrl() == null){
                elasticsearchVideoDto.setCoverUrl(book.getCoverUrl());
            }
            if(elasticsearchVideoDto.getTitle() == null){
                elasticsearchVideoDto.setTitle(book.getTitle());
            }
            if(elasticsearchVideoDto.getVideoTypeId() == null){
                elasticsearchVideoDto.setVideoTypeId(book.getVideoTypeId());
            }
            if(elasticsearchVideoDto.getModifyUserId() == null){
                elasticsearchVideoDto.setModifyUserId(book.getModifyUserId());
            }
            if(elasticsearchVideoDto.getCreateTime() == null){
                elasticsearchVideoDto.setCreateTime(book.getCreateTime());
            }
            if(elasticsearchVideoDto.getModifyTime() == null){
                elasticsearchVideoDto.setModifyTime(book.getModifyTime());
            }

        }
        elasticsearchVideoDao.save(elasticsearchVideoDto);
    }

    @Override
    public ElasticsearchVideoDto getVideo(Long id) {
        Optional<ElasticsearchVideoDto> optional = elasticsearchVideoDao.findById(id);
        return optional.get();
    }

    @Override
    public ResponseSearchVideoDto getVideo(String query, Integer pageNum, Integer pageSize) {
        if(pageNum == null || pageSize == null){
            pageNum = 1;
            pageSize = 10;
        }

        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(query, "title");
        Sort sort = new Sort(Sort.Direction.DESC,"modifyTime");
        PageRequest pageRequest = new PageRequest(pageNum - 1, pageSize, sort);
        Page<ElasticsearchVideoDto> search = elasticsearchVideoDao.search(multiMatchQueryBuilder, pageRequest);
        List<ElasticsearchVideoDto> elasticsearchVideoDtos = search.getContent();

        //组装结果
        PageInfo pageInfo = assemblePageInfo(elasticsearchVideoDtos, pageNum, pageSize, search.getTotalElements(), search.getTotalPages());

        ResponseSearchVideoDto responseSearchVideoDto = new ResponseSearchVideoDto();
        responseSearchVideoDto.setPageInfo(pageInfo);
        responseSearchVideoDto.setSuccess(true);
        return responseSearchVideoDto;
    }

    @Override
    public void addMusic(ElasticsearchMusicDto elasticsearchMusicDto) {
        elasticsearchMusicDao.save(elasticsearchMusicDto);
    }

    @Override
    public void delMusic(Long id) {
        elasticsearchMusicDao.deleteById(id);
    }

    @Override
    public void updateMusic(ElasticsearchMusicDto elasticsearchMusicDto) {
        Optional<ElasticsearchMusicDto> optional = elasticsearchMusicDao.findById(elasticsearchMusicDto.getId());
        if(optional != null){
            ElasticsearchMusicDto book = optional.get();
            if(elasticsearchMusicDto.getFileUrl() == null){
                elasticsearchMusicDto.setFileUrl(book.getFileUrl());
            }
            if(elasticsearchMusicDto.getCoverUrl() == null){
                elasticsearchMusicDto.setCoverUrl(book.getCoverUrl());
            }
            if(elasticsearchMusicDto.getName() == null){
                elasticsearchMusicDto.setName(book.getName());
            }
            if(elasticsearchMusicDto.getAuthor() == null){
                elasticsearchMusicDto.setAuthor(book.getAuthor());
            }
            if(elasticsearchMusicDto.getComposer() == null){
                elasticsearchMusicDto.setComposer(book.getComposer());
            }
            if(elasticsearchMusicDto.getSinger() == null){
                elasticsearchMusicDto.setSinger(book.getSinger());
            }
            if(elasticsearchMusicDto.getMusicTypeId() == null){
                elasticsearchMusicDto.setMusicTypeId(book.getMusicTypeId());
            }
            if(elasticsearchMusicDto.getModifyUserId() == null){
                elasticsearchMusicDto.setModifyUserId(book.getModifyUserId());
            }
            if(elasticsearchMusicDto.getCreateTime() == null){
                elasticsearchMusicDto.setCreateTime(book.getCreateTime());
            }
            if(elasticsearchMusicDto.getModifyTime() == null){
                elasticsearchMusicDto.setModifyTime(book.getModifyTime());
            }
        }
        elasticsearchMusicDao.save(elasticsearchMusicDto);
    }

    @Override
    public ElasticsearchMusicDto getMusic(Long id) {
        Optional<ElasticsearchMusicDto> optional = elasticsearchMusicDao.findById(id);
        return optional.get();
    }

    @Override
    public ResponseSearchMusicDto getMusic(String query, Integer pageNum, Integer pageSize) {
        if(pageNum == null || pageSize == null){
            pageNum = 1;
            pageSize = 10;
        }

        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(query, "name", "singer");
        Sort sort = new Sort(Sort.Direction.DESC,"modifyTime");
        PageRequest pageRequest = new PageRequest(pageNum - 1, pageSize, sort);
        Page<ElasticsearchMusicDto> search = elasticsearchMusicDao.search(multiMatchQueryBuilder, pageRequest);
        List<ElasticsearchMusicDto> elasticsearchMusicDtos = search.getContent();

        //组装结果
        PageInfo pageInfo = assemblePageInfo(elasticsearchMusicDtos, pageNum, pageSize, search.getTotalElements(), search.getTotalPages());

        ResponseSearchMusicDto responseSearchMusicDto = new ResponseSearchMusicDto();
        responseSearchMusicDto.setPageInfo(pageInfo);
        responseSearchMusicDto.setSuccess(true);
        return responseSearchMusicDto;
    }

    @Override
    public void addPicture(ElasticsearchPictureDto elasticsearchPictureDto) {
        elasticsearchPictureDao.save(elasticsearchPictureDto);
    }

    @Override
    public void delPicture(Long id) {
        elasticsearchPictureDao.deleteById(id);
    }

    @Override
    public void updatePicture(ElasticsearchPictureDto elasticsearchPictureDto) {
        Optional<ElasticsearchPictureDto> optional = elasticsearchPictureDao.findById(elasticsearchPictureDto.getId());
        if(optional != null){
            ElasticsearchPictureDto book = optional.get();
            if(elasticsearchPictureDto.getFileUrl() == null){
                elasticsearchPictureDto.setFileUrl(book.getFileUrl());
            }
            if(elasticsearchPictureDto.getTitle() == null){
                elasticsearchPictureDto.setTitle(book.getTitle());
            }
            if(elasticsearchPictureDto.getPictureTypeId() == null){
                elasticsearchPictureDto.setPictureTypeId(book.getPictureTypeId());
            }
            if(elasticsearchPictureDto.getModifyUserId() == null){
                elasticsearchPictureDto.setModifyUserId(book.getModifyUserId());
            }
            if(elasticsearchPictureDto.getCreateTime() == null){
                elasticsearchPictureDto.setCreateTime(book.getCreateTime());
            }
            if(elasticsearchPictureDto.getModifyTime() == null){
                elasticsearchPictureDto.setModifyTime(book.getModifyTime());
            }
        }
        elasticsearchPictureDao.save(elasticsearchPictureDto);
    }

    @Override
    public ElasticsearchPictureDto getPicture(Long id) {
        Optional<ElasticsearchPictureDto> optional = elasticsearchPictureDao.findById(id);
        return optional.get();
    }

    @Override
    public ResponseSearchPictureDto getPicture(String query, Integer pageNum, Integer pageSize) {
        if(pageNum == null || pageSize == null){
            pageNum = 1;
            pageSize = 10;
        }

        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(query, "title");
        Sort sort = new Sort(Sort.Direction.DESC,"modifyTime");
        PageRequest pageRequest = new PageRequest(pageNum - 1, pageSize, sort);
        Page<ElasticsearchPictureDto> search = elasticsearchPictureDao.search(multiMatchQueryBuilder, pageRequest);
        List<ElasticsearchPictureDto> elasticsearchPictureDtos = search.getContent();

        //组装结果
        PageInfo pageInfo = assemblePageInfo(elasticsearchPictureDtos, pageNum, pageSize, search.getTotalElements(), search.getTotalPages());

        ResponseSearchPictureDto responseSearchPictureDto = new ResponseSearchPictureDto();
        responseSearchPictureDto.setPageInfo(pageInfo);
        responseSearchPictureDto.setSuccess(true);
        return responseSearchPictureDto;
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
            User user = userService.findUserById(elasticsearchBlogDto.getUserId());

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
     * @param list
     * @param pageNum
     * @param pageSize
     * @param total
     * @param pages
     * @return
     */
    public PageInfo assemblePageInfo(List list, Integer pageNum, Integer pageSize, Long total, Integer pages){
        PageInfo pageInfo = new PageInfo(list);
        pageInfo.setTotal(total);
        pageInfo.setPages(pages);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setHasPreviousPage(pageNum.equals(1) ? false : true);
        pageInfo.setHasNextPage(pageNum.equals(pages) ? false : true);
        return pageInfo;
    }

    private void assembleBookDtos(List<ElasticsearchBookDto> elasticsearchBookDtos) {
        for(ElasticsearchBookDto book : elasticsearchBookDtos){
            if(book.getIntroduction().length() > 20){
                book.setIntroduction(book.getIntroduction().substring(0, 20) + "...");
            }
            //删除不必要信息
            book.setCatalog(null);
            book.setBookTypeId(null);
            book.setModifyUserId(null);
            book.setCreateTime(null);
            book.setModifyTime(null);
        }
    }

    private void assembleVideoDtos(List<ElasticsearchVideoDto> elasticsearchVideoDtos) {
        for(ElasticsearchVideoDto video : elasticsearchVideoDtos){
            if(video.getTitle().length() > 10){
                video.setTitle(video.getTitle().substring(0, 10) + "...");
            }
            //删除不必要信息
            video.setVideoTypeId(null);
            video.setModifyUserId(null);
            video.setCreateTime(null);
            video.setModifyTime(null);
        }
    }

    private void assembleMusicDtos(List<ElasticsearchMusicDto> elasticsearchMusicDtos) {
        for(ElasticsearchMusicDto music : elasticsearchMusicDtos){
            //删除不必要信息
            music.setAuthor(null);
            music.setComposer(null);
            music.setMusicTypeId(null);
            music.setModifyUserId(null);
            music.setCreateTime(null);
            music.setModifyTime(null);
        }
    }

    private void assemblePictureDtos(List<ElasticsearchPictureDto> elasticsearchPictureDtos) {
        for(ElasticsearchPictureDto picture : elasticsearchPictureDtos){
            //删除不必要信息
            if(picture.getTitle().length() > 10){
                picture.setTitle(picture.getTitle().substring(0, 10) + "...");
            }
            picture.setPictureTypeId(null);
            picture.setModifyUserId(null);
            picture.setCreateTime(null);
            picture.setModifyTime(null);
        }
    }
}

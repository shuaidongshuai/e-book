package com.dong.ebook.elasticsearch;

import com.dong.ebook.dao.BlogDao;
import com.dong.ebook.dto.ElasticsearchBlogDto;
import com.dong.ebook.esdao.ElasticsearchBlogDao;
import com.dong.ebook.model.BlogExample;
import com.dong.ebook.model.BlogWithBLOBs;
import org.dozer.DozerBeanMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Init {
    @Autowired
    ElasticsearchBlogDao elasticsearchBlogDao;

    @Autowired
    BlogDao blogDao;

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @Test
    public void delAll() {
        elasticsearchBlogDao.deleteAll();
    }

    @Test
    public void copyMysql(){
        List<BlogWithBLOBs> blogWithBLOBs = blogDao.selectByExampleWithBLOBs(new BlogExample());
        for(BlogWithBLOBs blog : blogWithBLOBs){
            ElasticsearchBlogDto elasticsearchBlogDto = BlogWithBLOBs2Elasticsearch(blog);
            elasticsearchBlogDao.save(elasticsearchBlogDto);
        }
    }

    public ElasticsearchBlogDto BlogWithBLOBs2Elasticsearch(BlogWithBLOBs blogWithBLOBs){
        return dozerBeanMapper.map(blogWithBLOBs, ElasticsearchBlogDto.class);
    }

}

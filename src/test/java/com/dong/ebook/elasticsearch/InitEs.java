package com.dong.ebook.elasticsearch;

import com.dong.ebook.dao.*;
import com.dong.ebook.dto.*;
import com.dong.ebook.esdao.*;
import com.dong.ebook.model.*;
import org.dozer.DozerBeanMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InitEs {
    @Autowired
    ElasticsearchBlogDao elasticsearchBlogDao;

    @Autowired
    ElasticsearchBookDao elasticsearchBookDao;

    @Autowired
    ElasticsearchVideoDao elasticsearchVideoDao;

    @Autowired
    ElasticsearchMusicDao elasticsearchMusicDao;

    @Autowired
    ElasticsearchPictureDao elasticsearchPictureDao;

    @Autowired
    BlogDao blogDao;

    @Autowired
    BookDao bookDao;

    @Autowired
    VideoDao videoDao;

    @Autowired
    MusicDao musicDao;

    @Autowired
    PictureDao pictureDao;

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @Test
    public void delAll() {
        elasticsearchBlogDao.deleteAll();
        elasticsearchBookDao.deleteAll();
        elasticsearchVideoDao.deleteAll();
        elasticsearchMusicDao.deleteAll();
        elasticsearchPictureDao.deleteAll();
    }

    @Test
    public void copyBlog(){
        List<BlogWithBLOBs> blogWithBLOBs = blogDao.selectByExampleWithBLOBs(new BlogExample());
        for(BlogWithBLOBs blog : blogWithBLOBs){
            ElasticsearchBlogDto elasticsearchBlogDto = dozerBeanMapper.map(blog, ElasticsearchBlogDto.class);
            elasticsearchBlogDao.save(elasticsearchBlogDto);
        }
    }

    @Test
    public void copyBook(){
        List<BookWithBLOBs> bookWithBLOBs = bookDao.selectByExampleWithBLOBs(new BookExample());
        for(BookWithBLOBs book : bookWithBLOBs){
            ElasticsearchBookDto elasticsearchBookDto = dozerBeanMapper.map(book, ElasticsearchBookDto.class);
            elasticsearchBookDao.save(elasticsearchBookDto);
        }
    }

    @Test
    public void copyVideo(){
        List<Video> videos = videoDao.selectByExample(new VideoExample());
        for(Video video : videos){
            ElasticsearchVideoDto elasticsearchVideoDto = dozerBeanMapper.map(video, ElasticsearchVideoDto.class);
            elasticsearchVideoDao.save(elasticsearchVideoDto);
        }
    }

    @Test
    public void copyMusic(){
        List<Music> musics = musicDao.selectByExample(new MusicExample());
        for(Music music : musics){
            ElasticsearchMusicDto elasticsearchMusicDto = dozerBeanMapper.map(music, ElasticsearchMusicDto.class);
            elasticsearchMusicDao.save(elasticsearchMusicDto);
        }
    }

    @Test
    public void copyPicture(){
        List<Picture> pictures = pictureDao.selectByExample(new PictureExample());
        for(Picture picture : pictures){
            ElasticsearchPictureDto elasticsearchPictureDto = dozerBeanMapper.map(picture, ElasticsearchPictureDto.class);
            elasticsearchPictureDao.save(elasticsearchPictureDto);
        }
    }

}

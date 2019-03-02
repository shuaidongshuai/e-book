package com.dong.ebook.common;

import com.dong.ebook.dao.*;
import com.dong.ebook.dto.*;
import com.dong.ebook.esdao.*;
import com.dong.ebook.model.*;
import com.dong.ebook.security.AuthUserService;
import org.apache.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InitEs {
    private static Logger logger = Logger.getLogger(InitEs.class);

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

    @Autowired
    AuthUserService authUserService;

    public boolean init(){
        User curUser = authUserService.getCurUser();
        if(curUser == null || !curUser.getRole().equals(UserRole.SUPERADMIN)){
            return false;
        }
        delAll();
        copyBlog();
        copyBook();
        copyVideo();
        copyMusic();
        copyPicture();
        logger.info("es初始化完成");
        return true;
    }

    public void delAll() {
        elasticsearchBlogDao.deleteAll();
        elasticsearchBookDao.deleteAll();
        elasticsearchVideoDao.deleteAll();
        elasticsearchMusicDao.deleteAll();
        elasticsearchPictureDao.deleteAll();
    }

    public void copyBlog(){
        List<BlogWithBLOBs> blogWithBLOBs = blogDao.selectByExampleWithBLOBs(new BlogExample());
        for(BlogWithBLOBs blog : blogWithBLOBs){
            ElasticsearchBlogDto elasticsearchBlogDto = dozerBeanMapper.map(blog, ElasticsearchBlogDto.class);
            elasticsearchBlogDao.save(elasticsearchBlogDto);
        }
    }

    public void copyBook(){
        List<BookWithBLOBs> bookWithBLOBs = bookDao.selectByExampleWithBLOBs(new BookExample());
        for(BookWithBLOBs book : bookWithBLOBs){
            ElasticsearchBookDto elasticsearchBookDto = dozerBeanMapper.map(book, ElasticsearchBookDto.class);
            elasticsearchBookDao.save(elasticsearchBookDto);
        }
    }

    public void copyVideo(){
        List<Video> videos = videoDao.selectByExample(new VideoExample());
        for(Video video : videos){
            ElasticsearchVideoDto elasticsearchVideoDto = dozerBeanMapper.map(video, ElasticsearchVideoDto.class);
            elasticsearchVideoDao.save(elasticsearchVideoDto);
        }
    }

    public void copyMusic(){
        List<Music> musics = musicDao.selectByExample(new MusicExample());
        for(Music music : musics){
            ElasticsearchMusicDto elasticsearchMusicDto = dozerBeanMapper.map(music, ElasticsearchMusicDto.class);
            elasticsearchMusicDao.save(elasticsearchMusicDto);
        }
    }

    public void copyPicture(){
        List<Picture> pictures = pictureDao.selectByExample(new PictureExample());
        for(Picture picture : pictures){
            ElasticsearchPictureDto elasticsearchPictureDto = dozerBeanMapper.map(picture, ElasticsearchPictureDto.class);
            elasticsearchPictureDao.save(elasticsearchPictureDto);
        }
    }
}

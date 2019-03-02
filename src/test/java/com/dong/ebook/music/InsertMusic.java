package com.dong.ebook.music;

import com.dong.ebook.dao.MusicDao;
import com.dong.ebook.model.Music;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InsertMusic {
    @Autowired
    MusicDao musicDao;

    @Test
    public void insert(){
        Date date = new Date();
        String content = "";
        String[] lines = content.split("\\|");
        for(String line : lines){
            try{
                String[] split = line.split("\\、");
                String fileUrl = split[0];
                String coverUrl = split[1];
                String name = split[2];
                String author = split[3];
                String composter = split[4];
                String singer = split[5];
                Long musicTypeId = Long.parseLong(split[6]);
                Long modifyUserId = Long.parseLong(split[7]);

                Music music = new Music();
                music.setFileUrl(fileUrl);
                music.setCoverUrl(coverUrl);
                music.setName(name);
                music.setAuthor(author);
                music.setComposer(composter);
                music.setSinger(singer);
                music.setMusicTypeId(musicTypeId);
                music.setModifyUserId(modifyUserId);
                music.setCreateTime(date);
                music.setModifyTime(date);
                musicDao.insertSelective(music);
            }catch (Exception e){
                System.out.println("insert error line=" + line);
            }

        }
    }
}

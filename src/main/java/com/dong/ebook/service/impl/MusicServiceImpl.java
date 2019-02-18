package com.dong.ebook.service.impl;

import com.dong.ebook.common.UserRole;
import com.dong.ebook.dao.MusicDao;
import com.dong.ebook.dto.*;
import com.dong.ebook.model.Music;
import com.dong.ebook.model.MusicExample;
import com.dong.ebook.model.User;
import com.dong.ebook.security.AuthUserService;
import com.dong.ebook.service.MusicService;
import com.dong.ebook.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MusicServiceImpl implements MusicService {
    private static Logger logger = Logger.getLogger(MusicServiceImpl.class);

    @Autowired
    MusicDao musicDao;

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @Autowired
    AuthUserService authUserService;

    @Autowired
    UserService userService;

    @Override
    public ResponseCommonDto saveMusic(RequestMusicDto requestMusicDto) {
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(false);

        Music music = RequestMusic2do(requestMusicDto);
        if(requestMusicDto.getId() == null || requestMusicDto.getId() < 1){
            if(getMusicByFileUrl(requestMusicDto.getFileUrl()) != null){
                responseCommonDto.setErrorMsg("此音乐已经创建");
                return responseCommonDto;
            }
            insertMusic(music);
        } else {
            updateMusicById(music);
        }
        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    @Override
    public ResponseCommonDto delMusic(long id) {
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(false);
        if(id < 1){
            logger.warn("delMusic id = " + id);
            responseCommonDto.setErrorMsg("id error");
            return responseCommonDto;
        }
        User curUser = authUserService.getCurUser();
        if (!UserRole.SUPERADMIN.equals(curUser.getRole())) {
            responseCommonDto.setErrorMsg("只有超级管理员可以删除音乐");
            return responseCommonDto;
        }
        musicDao.deleteByPrimaryKey(id);
        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    @Override
    public ResponseManagerMusicListDto getManagerMusicList(int pageNum, int pageSize, boolean desc) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        MusicExample musicExample = new MusicExample();
        if(desc){
            musicExample.setOrderByClause("modify_time desc");
        } else {
            musicExample.setOrderByClause("modify_time asc");
        }
        musicDao.selectByExample(musicExample);
        PageInfo pageInfo = new PageInfo(page.getResult());
        return assembleResponseManagerMusicListDto(pageInfo);
    }

    @Override
    public ResponseManagerMusicListDto getManagerMusicList(int pageNum, int pageSize, boolean desc, String query) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        MusicExample musicExample = assembleMusicExampleByDesc(desc);
        if(query != null && !query.isEmpty()){
            musicExample.createCriteria().andNameLike(query);
        }
        musicDao.selectByExample(musicExample);
        PageInfo pageInfo = new PageInfo(page.getResult());
        return assembleResponseManagerMusicListDto(pageInfo);
    }

    @Override
    public ResponseMusicDto getMusic(long id) {
        ResponseMusicDto responseMusicDto = new ResponseMusicDto();
        responseMusicDto.setSuccess(false);
        if(id < 1){
            responseMusicDto.setErrorMsg("id error");
            return responseMusicDto;
        }
        Music music = musicDao.selectByPrimaryKey(id);
        MusicDto musicDto = dozerBeanMapper.map(music, MusicDto.class);
        responseMusicDto.setMusicDto(musicDto);
        responseMusicDto.setSuccess(true);
        return responseMusicDto;
    }

    public Music getMusicByFileUrl(String fileUrl){
        MusicExample musicExample = new MusicExample();
        MusicExample.Criteria criteria = musicExample.createCriteria();
        criteria.andFileUrlEqualTo(fileUrl);
        List<Music> musics = musicDao.selectByExample(musicExample);
        if(musics.size() > 0){
            return musics.get(0);
        }
        return null;
    }

    public Music RequestMusic2do(RequestMusicDto requestMusicDto){
        return dozerBeanMapper.map(requestMusicDto, Music.class);
    }

    private void insertMusic(Music music) {
        Date date = new Date();
        music.setCreateTime(date);
        music.setModifyTime(date);
        music.setModifyUserId(authUserService.getCurUser().getId());
        musicDao.insertSelective(music);
    }

    private void updateMusicById(Music music) {
        music.setModifyTime(new Date());
        music.setModifyUserId(authUserService.getCurUser().getId());
        musicDao.updateByPrimaryKeySelective(music);
    }

    private ResponseManagerMusicListDto assembleResponseManagerMusicListDto(PageInfo pageInfo) {
        List<Music> musics = pageInfo.getList();
        List<ManagerMusicDto> managerMusicDtos = new ArrayList<>();
        for(Music music : musics){
            ManagerMusicDto managerMusicDto = dozerBeanMapper.map(music, ManagerMusicDto.class);
            managerMusicDtos.add(managerMusicDto);

            String userNickname = userService.findUserById(music.getModifyUserId()).getNickname();
            managerMusicDto.setModifyUserNickname(userNickname);
        }
        pageInfo.setList(managerMusicDtos);

        ResponseManagerMusicListDto responseManagerMusicListDto = new ResponseManagerMusicListDto();
        responseManagerMusicListDto.setPageInfo(pageInfo);
        responseManagerMusicListDto.setSuccess(true);
        return responseManagerMusicListDto;
    }

    public MusicExample assembleMusicExampleByDesc(boolean desc){
        MusicExample musicExample = new MusicExample();
        if(desc){
            musicExample.setOrderByClause("modify_time desc");
        } else {
            musicExample.setOrderByClause("modify_time asc");
        }
        return musicExample;
    }
}

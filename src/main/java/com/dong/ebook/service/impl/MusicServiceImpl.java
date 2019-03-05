package com.dong.ebook.service.impl;

import com.dong.ebook.common.PreferenceTypeName;
import com.dong.ebook.common.UserRole;
import com.dong.ebook.dao.MusicDao;
import com.dong.ebook.dto.*;
import com.dong.ebook.model.*;
import com.dong.ebook.security.AuthUserService;
import com.dong.ebook.service.ElasticsearchService;
import com.dong.ebook.service.MusicService;
import com.dong.ebook.service.PreferenceService;
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

    @Autowired
    PreferenceService preferenceService;

    @Autowired
    ElasticsearchService elasticsearchService;

    @Override
    public ResponseCommonDto saveMusic(RequestMusicDto requestMusicDto) {
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(false);

        if(authUserService.getCurUser().getIslock()){
            responseCommonDto.setErrorMsg("你已被上锁，不能上传任何信息");
            return responseCommonDto;
        }

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
        elasticsearchService.delMusic(id);
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

    @Override
    public ResponseMainPageMusicListDto getMainPageMusicList() {
        //主页的音乐分两部分 1.个性推荐 2.流行、英文、抖音
        //1
        ResponseMainPageMusicListDto mainPageMusicListDto = getMainPagePersonal();
        //2
        int popularMusicTypeId = 2, englishMusicTypeId = 9, douyinMusicTypeId = 11;
        List<MusicDto> popularMusic = getSimpleMusicByTypeId(1, 9, true, popularMusicTypeId);
        List<MusicDto> englishMusic = getSimpleMusicByTypeId(1, 9, true, englishMusicTypeId);
        List<MusicDto> douyinMusic = getSimpleMusicByTypeId(1, 9, true, douyinMusicTypeId);
        mainPageMusicListDto.setPopularMusic(popularMusic);
        mainPageMusicListDto.setEnglishMusic(englishMusic);
        mainPageMusicListDto.setDouyinMusic(douyinMusic);
        return mainPageMusicListDto;
    }

    private ResponseMainPageMusicListDto getMainPagePersonal(){
        int pageSize = 10, pageNum = 3;
        int totalSize = pageSize * pageNum;
        List<Music> musics;
        User user = authUserService.getCurUser();
        if(user == null){
            musics = getMusicList(1, totalSize, true);
        }else{
            //根据兴趣爱好找
            List<Long> typeIdList = preferenceService.getPreferenceTypeId(user.getId(), PreferenceTypeName.BOOK);
            musics = getMusicListByTypeId(1, totalSize, true, typeIdList);
            if(musics.size() < totalSize){
                //数量不够就找别的
                musics.addAll(getMusicListByNotTypeId(1, totalSize - musics.size(), true, typeIdList));
            }
        }
        if(musics.size() > totalSize){
            logger.info("getMainPageMusicList musics.size()=" + musics.size() + " > size=" + totalSize);
            musics = musics.subList(0, totalSize);
        }
        return assembleResponseMainPageMusicListDto(musics, pageSize);
    }

    private List<MusicDto> getSimpleMusicByTypeId(int pageNum, int pageSize, boolean desc, long typeId){
        List<Music> musics = getMusicByTypeId(pageNum, pageSize, desc, typeId);
        List<MusicDto> musicDtos = new ArrayList<>(pageSize);
        for(Music music : musics){
            music.setMusicTypeId(null);
            music.setAuthor(null);
            music.setComposer(null);
            music.setMusicTypeId(null);
            music.setCreateTime(null);
            music.setModifyTime(null);
            music.setModifyUserId(null);
            musicDtos.add(do2dto(music));
        }
        return musicDtos;
    }

    private ResponseMainPageMusicListDto assembleResponseMainPageMusicListDto(List<Music> musics, int pageSize) {
        List<MusicDto> firstPageMusic = new ArrayList<>();
        List<MusicDto> secondPageMusic = new ArrayList<>();
        List<MusicDto> thirdPageMusic = new ArrayList<>();
        int idx = 0;
        for(Music music : musics){
            //裁剪name
            if(music.getName().length() > 15){
                music.setName(music.getName().substring(0, 15) + "...");
            }
            //删除不需要的数据
            music.setCreateTime(null);
            music.setModifyTime(null);
            music.setMusicTypeId(null);
            music.setModifyUserId(null);
            music.setAuthor(null);
            music.setComposer(null);

            MusicDto musicDto = dozerBeanMapper.map(music, MusicDto.class);
            if(++idx <= pageSize){
                firstPageMusic.add(musicDto);
            }else if(idx <= pageSize * 2){
                secondPageMusic.add(musicDto);
            }else if (idx <= pageSize * 3){
                thirdPageMusic.add(musicDto);
            }else{
                break;
            }
        }
        ResponseMainPageMusicListDto responseMainPageMusicListDto = new ResponseMainPageMusicListDto();
        responseMainPageMusicListDto.setFirstPageMusic(firstPageMusic);
        responseMainPageMusicListDto.setSecondPageMusic(secondPageMusic);
        responseMainPageMusicListDto.setThirdPageMusic(thirdPageMusic);
        responseMainPageMusicListDto.setSuccess(true);
        return responseMainPageMusicListDto;
    }

    private List<Music> getMusicList(int pageNum, int pageSize, boolean desc) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        MusicExample musicExample = assembleMusicExampleByDesc(desc);
        musicDao.selectByExample(musicExample);
        return page.getResult();
    }

    private List<Music> getMusicByTypeId(int pageNum, int pageSize, boolean desc, long typeId){
        Page page = PageHelper.startPage(pageNum, pageSize);
        MusicExample musicExample = assembleMusicExampleByDesc(desc);
        musicExample.createCriteria().andMusicTypeIdEqualTo(typeId);
        musicDao.selectByExample(musicExample);
        return page.getResult();
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
        music.setTraffic(0);
        music.setCreateTime(date);
        music.setModifyTime(date);
        music.setModifyUserId(authUserService.getCurUser().getId());
        musicDao.insertSelective(music);
        elasticsearchService.addMusic(Music2Elasticsearch(music));
    }

    private void updateMusicById(Music music) {
        music.setModifyTime(new Date());
        music.setModifyUserId(authUserService.getCurUser().getId());
        musicDao.updateByPrimaryKeySelective(music);
        elasticsearchService.updateMusic(Music2Elasticsearch(music));
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

    public MusicDto do2dto(Music music){
        return dozerBeanMapper.map(music, MusicDto.class);
    }

    public ElasticsearchMusicDto Music2Elasticsearch(Music music){
        return dozerBeanMapper.map(music, ElasticsearchMusicDto.class);
    }

    public List<Music> getMusicListByTypeId(int pageNum, int pageSize, boolean desc, List<Long> typeIds) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        MusicExample musicExample = assembleMusicExampleByDesc(desc);
        if(typeIds.size() > 0){
            musicExample.createCriteria().andMusicTypeIdIn(typeIds);
        }
        musicDao.selectByExample(musicExample);
        return page.getResult();
    }

    public List<Music> getMusicListByNotTypeId(int pageNum, int pageSize, boolean desc, List<Long> typeIds) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        MusicExample musicExample = assembleMusicExampleByDesc(desc);
        if(typeIds.size() > 0){
            musicExample.createCriteria().andMusicTypeIdNotIn(typeIds);
        }
        musicDao.selectByExample(musicExample);
        return page.getResult();
    }
}

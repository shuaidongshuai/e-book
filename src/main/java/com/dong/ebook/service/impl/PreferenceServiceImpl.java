package com.dong.ebook.service.impl;

import com.dong.ebook.common.PreferenceTypeName;
import com.dong.ebook.dao.*;
import com.dong.ebook.dto.PreferenceDto;
import com.dong.ebook.dto.ResponseCommonDto;
import com.dong.ebook.dto.ResponsePreferenceDto;
import com.dong.ebook.model.*;
import com.dong.ebook.security.AuthUserService;
import com.dong.ebook.service.PreferenceService;
import org.apache.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PreferenceServiceImpl implements PreferenceService {
    private static Logger logger = Logger.getLogger(UserServiceImpl.class);

    @Autowired
    private AuthUserService authUserService;

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @Autowired
    private PreferenceDao preferenceDao;

    @Autowired
    private BookTypeDao bookTypeDao;

    @Autowired
    private VideoTypeDao videoTypeDao;

    @Autowired
    private MusicTypeDao musicTypeDao;

    @Autowired
    private PictureTypeDao pictureTypeDao;

    @Autowired
    private BlogTypeDao blogTypeDao;

    @Override
    public ResponsePreferenceDto getBookPreference() {
        List<Preference> preference = getPreference(authUserService.getCurUser().getId(), PreferenceTypeName.BOOK);
        return assemblePreferenceDtos(preference, PreferenceTypeName.BOOK);
    }

    @Override
    public ResponseCommonDto addBookPreference(long typeId) {
        insertPreference(typeId, PreferenceTypeName.BOOK);
        return assembleSuccessResponse();
    }

    @Override
    public ResponseCommonDto delBookPreference(long typeId) {
        deletePreference(typeId, PreferenceTypeName.BOOK);
        return assembleSuccessResponse();
    }

    @Override
    public ResponsePreferenceDto getVideoPreference() {
        List<Preference> preference = getPreference(authUserService.getCurUser().getId(), PreferenceTypeName.VIDEO);
        return assemblePreferenceDtos(preference, PreferenceTypeName.VIDEO);
    }

    @Override
    public ResponseCommonDto addVideoPreference(long typeId) {
        insertPreference(typeId, PreferenceTypeName.VIDEO);
        return assembleSuccessResponse();
    }

    @Override
    public ResponseCommonDto delVideoPreference(long typeId) {
        deletePreference(typeId, PreferenceTypeName.VIDEO);
        return assembleSuccessResponse();
    }

    @Override
    public ResponsePreferenceDto getMusicPreference() {
        List<Preference> preference = getPreference(authUserService.getCurUser().getId(), PreferenceTypeName.MUSIC);
        return assemblePreferenceDtos(preference, PreferenceTypeName.MUSIC);
    }

    @Override
    public ResponseCommonDto addMusicPreference(long typeId) {
        insertPreference(typeId, PreferenceTypeName.MUSIC);
        return assembleSuccessResponse();
    }

    @Override
    public ResponseCommonDto delMusicPreference(long typeId) {
        deletePreference(typeId, PreferenceTypeName.MUSIC);
        return assembleSuccessResponse();
    }

    @Override
    public ResponsePreferenceDto getPicturePreference() {
        List<Preference> preference = getPreference(authUserService.getCurUser().getId(), PreferenceTypeName.PICTURE);
        return assemblePreferenceDtos(preference, PreferenceTypeName.PICTURE);
    }

    @Override
    public ResponseCommonDto addPicturePreference(long typeId) {
        insertPreference(typeId, PreferenceTypeName.PICTURE);
        return assembleSuccessResponse();
    }

    @Override
    public ResponseCommonDto delPicturePreference(long typeId) {
        deletePreference(typeId, PreferenceTypeName.PICTURE);
        return assembleSuccessResponse();
    }

    @Override
    public ResponsePreferenceDto getBlogPreference() {
        List<Preference> preference = getPreference(authUserService.getCurUser().getId(), PreferenceTypeName.BLOG);
        return assemblePreferenceDtos(preference, PreferenceTypeName.BLOG);
    }

    @Override
    public ResponseCommonDto addBlogPreference(long typeId) {
        insertPreference(typeId, PreferenceTypeName.BLOG);
        return assembleSuccessResponse();
    }

    @Override
    public ResponseCommonDto delBlogPreference(long typeId) {
        deletePreference(typeId, PreferenceTypeName.BLOG);
        return assembleSuccessResponse();
    }


    public List<Preference> getPreference(long userId, String typeName) {
        PreferenceExample preferenceExample = new PreferenceExample();
        preferenceExample.createCriteria().andUserIdEqualTo(userId).andTypeNameEqualTo(typeName);
        List<Preference> preferences = preferenceDao.selectByExample(preferenceExample);
        return preferences;
    }

    public List<Preference> getPreference(long userId, long typeId, String typeName) {
        PreferenceExample preferenceExample = new PreferenceExample();
        preferenceExample.createCriteria().andUserIdEqualTo(userId)
                .andTypeIdEqualTo(typeId).andTypeNameEqualTo(typeName);
        return preferenceDao.selectByExample(preferenceExample);
    }

    public void insertPreference(long typeId, String typeName) {
        Long userId = authUserService.getCurUser().getId();

        List<Preference> preferences = getPreference(userId, typeId, typeName);
        if (preferences.size() > 0){
            logger.info("insertPreference typeId=" + typeId + " typeName=" + typeName + " 已经存在");
            return;
        }

        Preference preference = new Preference();
        preference.setUserId(userId);
        preference.setTypeId(typeId);
        preference.setTypeName(typeName);

        preferenceDao.insertSelective(preference);
    }

    public void deletePreference(long typeId, String typeName) {
        Long userId = authUserService.getCurUser().getId();
        PreferenceExample preferenceExample = new PreferenceExample();
        preferenceExample.createCriteria().andUserIdEqualTo(userId).andTypeIdEqualTo(typeId)
                .andTypeNameEqualTo(typeName);
        preferenceDao.deleteByExample(preferenceExample);
    }

    public ResponsePreferenceDto assemblePreferenceDtos(List<Preference> preferences, String typeName){
        List types = null;
        if(PreferenceTypeName.BOOK.equals(typeName)){
            List<BookType> bookTypes = bookTypeDao.selectByExample(new BookTypeExample());
            types = bookTypes;
        }else if(PreferenceTypeName.VIDEO.equals(typeName)){
            List<VideoType> videoTypes = videoTypeDao.selectByExample(new VideoTypeExample());
            types = videoTypes;
        }else if(PreferenceTypeName.MUSIC.equals(typeName)){
            List<MusicType> musicTypes = musicTypeDao.selectByExample(new MusicTypeExample());
            types = musicTypes;
        }else if(PreferenceTypeName.PICTURE.equals(typeName)){
            List<PictureType> pictureTypes = pictureTypeDao.selectByExample(new PictureTypeExample());
            types = pictureTypes;
        }else if(PreferenceTypeName.BLOG.equals(typeName)){
            List<BlogType> blogTypes = blogTypeDao.selectByExample(new BlogTypeExample());
            types = blogTypes;
        }

        Set<Long> preferenceSet = new HashSet<>(preferences.size() * 2);
        for(Preference p : preferences){
            preferenceSet.add(p.getTypeId());
        }

        List<PreferenceDto> preferenceDtos = new ArrayList<>(preferences.size());
        Long id = null;
        String className = null;
        for(Object o : types){
            if(o instanceof BookType){
                id = ((BookType) o).getId();
                className = ((BookType) o).getClassName();
            } else if(o instanceof VideoType){
                id = ((VideoType) o).getId();
                className = ((VideoType) o).getClassName();
            } else if(o instanceof MusicType){
                id = ((MusicType) o).getId();
                className = ((MusicType) o).getClassName();
            } else if(o instanceof PictureType){
                id = ((PictureType) o).getId();
                className = ((PictureType) o).getClassName();
            } else if(o instanceof BlogType){
                id = ((BlogType) o).getId();
                className = ((BlogType) o).getClassName();
            }
            PreferenceDto responsePreferenceDto = new PreferenceDto();
            preferenceDtos.add(responsePreferenceDto);

            if(preferenceSet.contains(id)){
                responsePreferenceDto.setChecked(true);
            }else{
                responsePreferenceDto.setChecked(false);
            }
            responsePreferenceDto.setTypeId(id);
            responsePreferenceDto.setClassName(className);
        }

        ResponsePreferenceDto responsePreferenceDtos = new ResponsePreferenceDto();
        responsePreferenceDtos.setPreferenceDtos(preferenceDtos);
        responsePreferenceDtos.setSuccess(true);
        return responsePreferenceDtos;
    }

    public ResponseCommonDto assembleSuccessResponse(){
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }
}

package com.dong.ebook.service.impl;

import com.dong.ebook.dao.ConcernDao;
import com.dong.ebook.dao.UserDao;
import com.dong.ebook.dto.*;
import com.dong.ebook.model.*;
import com.dong.ebook.security.AuthUserService;
import com.dong.ebook.service.BlogExtraMsgCache;
import com.dong.ebook.service.ConcernService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ConcernServiceImpl implements ConcernService {
    private static Logger logger = Logger.getLogger(UserServiceImpl.class);

    @Autowired
    private ConcernDao concernDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthUserService authUserService;

    @Autowired
    private BlogExtraMsgCache blogExtraMsgCache;

    @Override
    public ResponseCommonDto concernUser(Long userId) {
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(false);

        User curUser = authUserService.getCurUser();
        if(curUser == null){
            logger.warn("concernUser 用户没有登录 concernUserId = " + userId);
            responseCommonDto.setErrorMsg("用户没有登录");
            return responseCommonDto;
        }

        //查询
        Concern concern = getConcern(curUser.getId(), userId);
        if(concern != null){
            logger.warn("concernUser 该用户已经关注过 userId = " + curUser.getId() + " concernUserId" + userId);
            responseCommonDto.setErrorMsg("该用户已经关注过");
            return responseCommonDto;
        }

        Date date = new Date();
        concern = new Concern();
        concern.setFromUserId(curUser.getId());
        concern.setToUserId(userId);
        concern.setCreateTime(date);
        concern.setModifyTime(date);
        concernDao.insert(concern);

        //修改缓存
        blogExtraMsgCache.addFansNum(userId, 1);
        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    @Override
    public ResponseCommonDto cancelConcernUser(Long userId) {
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(false);

        User curUser = authUserService.getCurUser();
        if(curUser == null){
            logger.warn("cancelConcernUser 用户没有登录 concernUserId = " + userId);
            responseCommonDto.setErrorMsg("用户没有登录");
            return responseCommonDto;
        }

        ConcernExample concernExample = new ConcernExample();
        ConcernExample.Criteria criteria = concernExample.createCriteria();
        criteria.andFromUserIdEqualTo(curUser.getId()).andToUserIdEqualTo(userId);
        concernDao.deleteByExample(concernExample);

        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    @Override
    public ResponseCommonDto haveConcern(Long fromId, Long toId) {
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(false);

        Concern concern = getConcern(fromId, toId);
        if(concern != null){
            responseCommonDto.setSuccess(true);
        }
        return responseCommonDto;
    }

    @Override
    public List<Concern> findFans(Long userId) {
        ConcernExample concernExample = new ConcernExample();
        ConcernExample.Criteria criteria = concernExample.createCriteria();
        criteria.andToUserIdEqualTo(userId);
        return  concernDao.selectByExample(concernExample);
    }

    @Override
    public ResponseConcernListDto getConcernUserList(int pageNum, int pageSize, boolean desc, String query) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        ConcernExample concernExample = assembleConcernExampleByDesc(desc);
        concernExample.createCriteria().andFromUserIdEqualTo(authUserService.getCurUser().getId());
        concernDao.selectByExample(concernExample);
        PageInfo pageInfo = new PageInfo(page.getResult());
        return assembleResponseCFUserListDto(pageInfo, query, true);
    }

    @Override
    public ResponseConcernListDto getFansUserList(int pageNum, int pageSize, boolean desc, String query) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        ConcernExample concernExample = assembleConcernExampleByDesc(desc);
        concernExample.createCriteria().andToUserIdEqualTo(authUserService.getCurUser().getId());
        concernDao.selectByExample(concernExample);
        PageInfo pageInfo = new PageInfo(page.getResult());
        return assembleResponseCFUserListDto(pageInfo, query, false);
    }

    public Concern getConcern(Long fromId, Long toId) {
        ConcernExample concernExample = new ConcernExample();
        ConcernExample.Criteria criteria = concernExample.createCriteria();
        criteria.andFromUserIdEqualTo(fromId).andToUserIdEqualTo(toId);
        List<Concern> concerns = concernDao.selectByExample(concernExample);
        int size = concerns.size();
        if(size == 0){
            return null;
        } else if(size > 1){
            logger.error("getConcern size = " + size + " fromId = " + fromId + " toId =" + toId);
        }
        return concerns.get(0);
    }

    public ConcernExample assembleConcernExampleByDesc(boolean desc){
        ConcernExample concernExample = new ConcernExample();
        if(desc){
            concernExample.setOrderByClause("create_time desc");
        } else {
            concernExample.setOrderByClause("create_time asc");
        }
        return concernExample;
    }

    private ResponseConcernListDto assembleResponseCFUserListDto(PageInfo pageInfo, String searchUserNickname, boolean isConcern) {
        List<Concern> concerns = pageInfo.getList();
        List<ConcernUserDto> concernUserDtos = new ArrayList<>(concerns.size());
        List<ConcernUserDto> searchConcernUserDtos = new ArrayList<>(concerns.size());
        Long userId;
        for(Concern concern : concerns){
            if(isConcern){
                userId = concern.getToUserId();
            }else{
                userId = concern.getFromUserId();
            }
            User user = userDao.selectByPrimaryKey(userId);

            ConcernUserDto concernUserDto = new ConcernUserDto();
            concernUserDtos.add(concernUserDto);
            if(searchUserNickname != null && user.getNickname().equals(searchUserNickname)){
                searchConcernUserDtos.add(concernUserDto);
            }

            concernUserDto.setUserId(userId);
            concernUserDto.setCreateTime(concern.getCreateTime());
            concernUserDto.setUserNickname(user.getNickname());
            concernUserDto.setUserEmail(user.getEmail());
            concernUserDto.setUserAvatar(user.getAvatar());
        }

        if(searchConcernUserDtos.size() > 0){
            pageInfo.setList(searchConcernUserDtos);
        }else{
            pageInfo.setList(concernUserDtos);
        }

        ResponseConcernListDto responseConcernListDto = new ResponseConcernListDto();
        responseConcernListDto.setPageInfo(pageInfo);
        responseConcernListDto.setSuccess(true);
        return responseConcernListDto;
    }
}

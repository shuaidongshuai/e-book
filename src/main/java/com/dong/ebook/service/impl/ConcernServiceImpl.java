package com.dong.ebook.service.impl;

import com.dong.ebook.dao.ConcernDao;
import com.dong.ebook.dto.ResponseCommonDto;
import com.dong.ebook.model.Concern;
import com.dong.ebook.model.ConcernExample;
import com.dong.ebook.model.User;
import com.dong.ebook.security.AuthUserService;
import com.dong.ebook.service.BlogExtraMsgCache;
import com.dong.ebook.service.ConcernService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConcernServiceImpl implements ConcernService {
    private static Logger logger = Logger.getLogger(UserServiceImpl.class);

    @Autowired
    private ConcernDao concernDao;

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

        concern = new Concern();
        concern.setFromUserId(curUser.getId());
        concern.setToUserId(userId);
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

}

package com.dong.ebook.service.impl;

import com.dong.ebook.dao.VoteDao;
import com.dong.ebook.dto.ResponseCommonDto;
import com.dong.ebook.model.Vote;
import com.dong.ebook.model.VoteExample;
import com.dong.ebook.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class VoteServiceImpl implements VoteService {
    @Autowired
    private VoteDao voteDao;

    @Override
    public ResponseCommonDto vote(Long userId, Long blogId) {
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(false);
        if(checkVote(userId, blogId)){
            responseCommonDto.setErrorMsg("已经投过票了");
            return responseCommonDto;
        }

        Date date = new Date();
        Vote vote = new Vote();
        vote.setUserId(userId);
        vote.setBlogId(blogId);
        vote.setCreateTime(date);
        vote.setModifyTime(date);
        voteDao.insert(vote);

        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    @Override
    public ResponseCommonDto cancelVote(Long userId, Long blogId) {
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(false);
        if(!checkVote(userId, blogId)){
            responseCommonDto.setErrorMsg("没有投过票");
            return responseCommonDto;
        }

        VoteExample voteExample = new VoteExample();
        VoteExample.Criteria criteria = voteExample.createCriteria();
        criteria.andUserIdEqualTo(userId).andBlogIdEqualTo(blogId);
        voteDao.deleteByExample(voteExample);

        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    @Override
    public Boolean checkVote(Long userId, Long blogId) {
        VoteExample voteExample = new VoteExample();
        VoteExample.Criteria criteria = voteExample.createCriteria();
        criteria.andUserIdEqualTo(userId).andBlogIdEqualTo(blogId);
        List<Vote> votes = voteDao.selectByExample(voteExample);
        if(votes.size() == 0){
            return false;
        }
        return true;
    }
}

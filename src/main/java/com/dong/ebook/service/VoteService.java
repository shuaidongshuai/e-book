package com.dong.ebook.service;

import com.dong.ebook.dto.ResponseCommonDto;

public interface VoteService {
    ResponseCommonDto vote(Long userId, Long blogId);
    ResponseCommonDto cancelVote(Long userId, Long blogId);
    Boolean checkVote(Long userId, Long blogId);
}

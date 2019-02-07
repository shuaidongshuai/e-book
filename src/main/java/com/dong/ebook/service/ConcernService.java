package com.dong.ebook.service;

import com.dong.ebook.dto.ResponseCommonDto;
import com.dong.ebook.model.Concern;

import java.util.List;

public interface ConcernService {
    /**
     * 关注
     * @param userId
     * @return
     */
    ResponseCommonDto concernUser(Long userId);

    /**
     * 取消关注
     * @param userId
     * @return
     */
    ResponseCommonDto cancelConcernUser(Long userId);

    /**
     * 查看是否关注过
     * @param fromId
     * @param toId
     * @return
     */
    ResponseCommonDto haveConcern(Long fromId, Long toId);

    /**
     * 查找自己的粉丝
     * @param userId
     * @return
     */
    List<Concern> findFans(Long userId);
}

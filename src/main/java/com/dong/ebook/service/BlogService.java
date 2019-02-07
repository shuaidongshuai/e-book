package com.dong.ebook.service;

import com.dong.ebook.dto.*;

public interface BlogService {
    /**
     * 保存blog
     * @param blogDto
     * @return
     */
    ResponseBlogSaveDto saveBlog(BlogDto blogDto);

    /**
     * 删除blog
     * @return
     */
    ResponseCommonDto deleteBlog(Long blogId);

    /**
     * 获取blog页面信息
     * @param blogId
     * @return
     */
    ResponseBlogPageDto getUserBlogById(Long blogId);

    /**
     * 获取某个用户的blog列表
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResponseUserBlogListDto getUserBlogList(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取blog列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResponseBlogListDto getBlogList(Integer pageNum, Integer pageSize);

    /**
     * 获取管理页面的blog列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResponseManagerBlogListDto getManagerBlogList(Integer pageNum, Integer pageSize);

    /**
     * 模糊查询blog列表
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    ResponseManagerBlogListDto getManagerBlogList(Integer pageNum, Integer pageSize, String query);

    /**
     * 编辑博客
     * @param blogId
     * @return
     */
    ResponseBlogEditDto editBlog(Long blogId);

    /**
     * 点赞
     * @param blogId
     * @return
     */
    ResponseCommonDto voteBlog(Long blogId);

    /**
     * 取消点赞
     * @param blogId
     * @return
     */
    ResponseCommonDto cancelVoteBlog(Long blogId);
}

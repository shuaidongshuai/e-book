package com.dong.ebook.service;

import com.dong.ebook.dto.*;

import java.util.List;

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
    ResponseCommonDto deleteBlog(long blogId);

    /**
     * 获取blog页面信息
     * @param blogId
     * @return
     */
    ResponseBlogPageDto getUserBlogById(long blogId);

    /**
     * 获取某个用户的blog列表
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResponseUserBlogListDto getUserBlogList(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取管理页面的blog列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResponseManagerBlogListDto getManagerBlogList(int pageNum, int pageSize, boolean desc);

    /**
     * 模糊查询blog列表
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    ResponseManagerBlogListDto getManagerBlogList(int pageNum, int pageSize, boolean desc, String query);

    /**
     * 编辑博客
     * @param blogId
     * @return
     */
    ResponseBlogEditDto editBlog(long blogId);

    /**
     * 点赞
     * @param blogId
     * @return
     */
    ResponseCommonDto voteBlog(long blogId);

    /**
     * 取消点赞
     * @param blogId
     * @return
     */
    ResponseCommonDto cancelVoteBlog(long blogId);

    /**
     * 主页博客
     * @return
     */
    ResponseBlogListDto getMainPageBlogList();

    /**
     * 获取热门blog
     * @param size
     * @return
     */
    List<BlogDto> getHotBlog(Integer size);
}

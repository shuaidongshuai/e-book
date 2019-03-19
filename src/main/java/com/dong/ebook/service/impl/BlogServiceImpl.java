package com.dong.ebook.service.impl;

import com.dong.ebook.common.PreferenceTypeName;
import com.dong.ebook.common.UserRole;
import com.dong.ebook.dao.BlogDao;
import com.dong.ebook.dto.*;
import com.dong.ebook.model.*;
import com.dong.ebook.security.AuthUserService;
import com.dong.ebook.service.*;
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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BlogServiceImpl implements BlogService {
    private static Logger logger = Logger.getLogger(BlogServiceImpl.class);

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @Autowired
    private BlogDao blogDao;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthUserService authUserService;

    @Autowired
    private ConcernService concernService;

    @Autowired
    private BlogExtraMsgCache blogExtraMsgCache;

    @Autowired
    private VoteService voteService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private PreferenceService preferenceService;

    private Lock lock = new ReentrantLock();

    @Override
    public ResponseBlogSaveDto saveBlog(BlogDto blogDto) {
        ResponseBlogSaveDto responseBlogSaveDto = new ResponseBlogSaveDto();
        responseBlogSaveDto.setSuccess(false);
        User user = authUserService.getCurUser();
        if(user.getIslock()){
            responseBlogSaveDto.setErrorMsg("你已被上锁不能保存博客，请联系管理员");
            return responseBlogSaveDto;
        }

        //设置用户上传blog数量为200
        Long userId = user.getId();
        int userBlogCount = getUserBlogCount(userId);
        if(userBlogCount >= 200){
            responseBlogSaveDto.setErrorMsg("用户上传博客数量最多为200篇");
            return responseBlogSaveDto;
        }

        blogDto.setUserId(userId);
        BlogWithBLOBs blogWithBLOBs = BlogDto2do(blogDto);

        //自动生成摘要
        String contentHtml = blogWithBLOBs.getContentHtml();
        //删除html标签
        String txtcontent = contentHtml.replaceAll("</?[^>]+>", ""); //剔出<html>的标签
        String summary = txtcontent.replaceAll("<a>\\s*|\t|\r|\n</a>", "");
        //目前只是截取前面250个字
        if(txtcontent.length() > 200){
            summary = summary.substring(0, 200);
        }

        /**
         * 这里有个重要的bug，用户如果提交了<script></script> <style></style> 会修改自己的页面
         */
        contentHtml = changeScriptStyle(contentHtml);
        blogWithBLOBs.setContentHtml(contentHtml);

        Date date = new Date();
        blogWithBLOBs.setModifyTime(date);
        blogWithBLOBs.setSummary(summary);
        if(blogWithBLOBs.getId() == null || blogWithBLOBs.getId() < 1){
            //初始化
            blogWithBLOBs.setCreateTime(date);
            blogWithBLOBs.setTraffic(0);
            blogWithBLOBs.setVoteNum(0);
            blogWithBLOBs.setCommentNum(0);
            blogDao.insertSelective(blogWithBLOBs);
            elasticsearchService.addBlog(BlogWithBLOBs2Elasticsearch(blogWithBLOBs));
        } else{
            blogDao.updateByPrimaryKeySelective(blogWithBLOBs);
            elasticsearchService.updateBlog(BlogWithBLOBs2Elasticsearch(blogWithBLOBs));
        }

        responseBlogSaveDto.setSuccess(true);
        responseBlogSaveDto.setBlogId(blogWithBLOBs.getId());
        return responseBlogSaveDto;
    }

    @Override
    public ResponseCommonDto deleteBlog(long blogId) {
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(false);

        //查询这篇bolg的主人
        ElasticsearchBlogDto blog = elasticsearchService.getBlog(blogId);
        if(blog == null){
            responseCommonDto.setErrorMsg("没有该博客");
            return responseCommonDto;
        }
        Long blogUserId = blog.getUserId();
        //检查是否有权限删除blog
        User curUser = authUserService.getCurUser();
        String role = curUser.getRole();
        if(!blogUserId.equals(curUser.getId())) {
            if(UserRole.USER.equals(role)){
                responseCommonDto.setErrorMsg("没有权限删除该博客");
                return responseCommonDto;
            }
            if(UserRole.ADMIN.equals(role)){
                User user = userService.findUserById(blogUserId);
                if(!UserRole.USERSHOW.equals(user.getRole())){
                    responseCommonDto.setErrorMsg("没有权限删除该博客");
                    return responseCommonDto;
                }
            }
        }

        blogDao.deleteByPrimaryKey(blogId);
        elasticsearchService.delBlog(blogId);

        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    @Override
    public ResponseBlogPageDto getUserBlogById(long blogId) {
        ResponseBlogPageDto responseBlogPageDto = new ResponseBlogPageDto();

        //每次进入一个完整blog页面都要修改阅读数
        BlogWithBLOBs blogWithBLOBs = blogDao.selectByPrimaryKey(blogId);
        if(blogWithBLOBs == null){
            responseBlogPageDto.setSuccess(false);
            responseBlogPageDto.setErrorMsg("没有这篇文章");
            return responseBlogPageDto;
        }

        //blogUserId
        Long blogUserId = blogWithBLOBs.getUserId();

        //获取除blog以外的扩展信息
        List<Integer> extraMsg = getUserBlogExtraMsg(blogUserId);

        //获取用户的信息
        User user = userService.findUserById(blogUserId);

        //前端展示nickname不超过13个字符
        String nickname = user.getNickname();
        if(nickname.length() > 13){
            nickname = nickname.substring(0, 13) + "...";
        }

        //检查是否是当前用户的blog
        Boolean selfBlog = false;

        //是否关注过博客的主人
        Boolean concern = false;

        //是否点赞过这篇文章
        Boolean vote = false;

        User curUser = authUserService.getCurUser();
        if(curUser != null){
            selfBlog = isSelf(curUser.getId(), blogUserId);
            if(!selfBlog){
                ResponseCommonDto responseCommonDto = concernService.haveConcern(curUser.getId(), blogUserId);
                if(responseCommonDto.isSuccess()){
                    concern = true;
                }
                //不是博主，文章阅读数+1
                addBlogTraffic(blogId, 1);
            }
            //是否点赞
            vote = voteService.checkVote(curUser.getId(), blogId);
        } else{
            //不是博主，文章阅读数+1
            addBlogTraffic(blogId, 1);
        }

        //不需要返回content
        blogWithBLOBs.setContent("");

        //do->dto
        BlogDto blogDto = BlogWithBLOBsDo2dto(blogWithBLOBs);

        responseBlogPageDto.setBlogDto(blogDto);
        responseBlogPageDto.setArticleNum(extraMsg.get(0));
        responseBlogPageDto.setFansNum(extraMsg.get(1));
        responseBlogPageDto.setVoteNum(extraMsg.get(2));
        responseBlogPageDto.setCommentNum(extraMsg.get(3));
        responseBlogPageDto.setNickname(nickname);
        responseBlogPageDto.setAvatar(user.getAvatar());
        responseBlogPageDto.setSelfBlog(selfBlog);
        responseBlogPageDto.setConcern(concern);
        responseBlogPageDto.setVote(vote);
        responseBlogPageDto.setSuccess(true);
        return responseBlogPageDto;
    }

    @Override
    public ResponseUserBlogListDto getUserBlogList(Long userId, Integer pageNum, Integer pageSize) {
        ResponseUserBlogListDto responseUserBlogListDto = new ResponseUserBlogListDto();
        responseUserBlogListDto.setSuccess(false);

        if(pageNum == null || pageSize == null){
            pageNum = 1;
            pageSize = 5;
        }

        if(userId == null){
            User curUser = authUserService.getCurUser();
            if(curUser == null){
                responseUserBlogListDto.setErrorMsg("您还未登陆");
                return responseUserBlogListDto;
            }
            userId = curUser.getId();
        }

        //user
        User user = userService.findUserById(userId);
        if(user == null){
            responseUserBlogListDto.setErrorMsg("没用此用户");
            return responseUserBlogListDto;
        }

        Boolean selfBlog = false;
        Boolean concern = false;
        User curUser = authUserService.getCurUser();
        if(curUser != null){
            if(curUser.getId().equals(userId)){
                selfBlog = true;
            }

            ResponseCommonDto responseCommonDto = concernService.haveConcern(curUser.getId(), userId);
            if(responseCommonDto.isSuccess()){
                concern = true;
            }
        }



        PageInfo pageInfo = getBlogListByUserId(userId, pageNum, pageSize);
        List<BlogDto> blogDtos = BlogDos2dto(pageInfo.getList());
        pageInfo.setList(blogDtos);

        //获取除blog以外的扩展信息
        List<Integer> extraMsg = getUserBlogExtraMsg(userId);

        responseUserBlogListDto.setPageInfo(pageInfo);
        responseUserBlogListDto.setUserId(userId);
        responseUserBlogListDto.setArticleNum(extraMsg.get(0));
        responseUserBlogListDto.setFansNum(extraMsg.get(1));
        responseUserBlogListDto.setVoteNum(extraMsg.get(2));
        responseUserBlogListDto.setCommentNum(extraMsg.get(3));
        responseUserBlogListDto.setNickname(user.getNickname());
        responseUserBlogListDto.setAvatar(user.getAvatar());
        responseUserBlogListDto.setSelfBlog(selfBlog);
        responseUserBlogListDto.setConcern(concern);
        responseUserBlogListDto.setSuccess(true);
        return responseUserBlogListDto;
    }

    @Override
    public ResponseManagerBlogListDto getManagerBlogList(int pageNum, int pageSize, boolean desc) {
        return getManagerBlogList(pageNum, pageSize, desc, null);
    }

    @Override
    public ResponseManagerBlogListDto getManagerBlogList(int pageNum, int pageSize, boolean desc, String query) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        BlogExample blogExample = assembleBlogExampleByModifyTimeDesc(desc);
        BlogExample.Criteria criteria = blogExample.createCriteria();
        if(query != null && !query.isEmpty()){
            try {
                Long id = Long.parseLong(query);
                criteria.andIdEqualTo(id);
            }catch (Exception e){
                criteria.andTitleLike(query);
            }
        }
        blogDao.selectByExample(blogExample);
        PageInfo pageInfo = new PageInfo(page.getResult());
        return assembleResponseManagerBlogListDto(pageInfo);
    }

    @Override
    public ResponseBlogEditDto editBlog(long blogId) {
        ResponseBlogEditDto responseBlogEditDto = new ResponseBlogEditDto();
        responseBlogEditDto.setSuccess(false);

        User curUser = authUserService.getCurUser();
        String title = "", content = "";
        if(blogId > 0){
            BlogWithBLOBs blogWithBLOBs = blogDao.selectByPrimaryKey(blogId);
            //检查是否是博主
            if(!blogWithBLOBs.getUserId().equals(curUser.getId())){
                logger.warn("有人恶意修改他人blog userId=" + curUser.getId());
                responseBlogEditDto.setErrorMsg("请不要修改别人博客");
                return responseBlogEditDto;
            }
            title = blogWithBLOBs.getTitle();
            content = blogWithBLOBs.getContent();
        }

        responseBlogEditDto.setBlogId(blogId);
        responseBlogEditDto.setTitle(title);
        responseBlogEditDto.setContent(content);
        responseBlogEditDto.setUserId(curUser.getId());
        responseBlogEditDto.setNickName(curUser.getNickname());
        responseBlogEditDto.setAvatar(curUser.getAvatar());
        responseBlogEditDto.setSuccess(true);
        return responseBlogEditDto;
    }

    @Override
    public ResponseCommonDto voteBlog(long blogId) {
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(false);
        User curUser = authUserService.getCurUser();
        if(curUser == null){
            logger.warn("voteBlog 用户没有登录");
            responseCommonDto.setErrorMsg("用户没有登录");
            return responseCommonDto;
        }

        //vote
        ResponseCommonDto vote = voteService.vote(curUser.getId(), blogId);
        if(vote.isSuccess()){
            //blog
            BlogWithBLOBs blogWithBLOBs = blogDao.selectByPrimaryKey(blogId);
            if(blogWithBLOBs == null){
                responseCommonDto.setErrorMsg("没有此博客");
                return responseCommonDto;
            }
            blogWithBLOBs.setVoteNum(blogWithBLOBs.getVoteNum() + 1);
            blogDao.updateByPrimaryKeySelective(blogWithBLOBs);
            elasticsearchService.updateBlog(BlogWithBLOBs2Elasticsearch(blogWithBLOBs));
        } else {
            responseCommonDto.setErrorMsg(vote.getErrorMsg());
            return responseCommonDto;
        }

        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    @Override
    public ResponseCommonDto cancelVoteBlog(long blogId) {
        ResponseCommonDto responseCommonDto = new ResponseCommonDto();
        responseCommonDto.setSuccess(false);
        User curUser = authUserService.getCurUser();
        if(curUser == null){
            logger.warn("cancelVoteBlog 用户没有登录");
            responseCommonDto.setErrorMsg("用户没有登录");
            return responseCommonDto;
        }

        //vote
        ResponseCommonDto cancelVote = voteService.cancelVote(curUser.getId(), blogId);
        if(cancelVote.isSuccess()){
            //blog
            BlogWithBLOBs blogWithBLOBs = blogDao.selectByPrimaryKey(blogId);
            if(blogWithBLOBs == null){
                responseCommonDto.setErrorMsg("没有此博客");
                return responseCommonDto;
            }
            blogWithBLOBs.setVoteNum(blogWithBLOBs.getVoteNum() - 1);
            blogDao.updateByPrimaryKeySelective(blogWithBLOBs);
            elasticsearchService.updateBlog(BlogWithBLOBs2Elasticsearch(blogWithBLOBs));
        } else {
            responseCommonDto.setErrorMsg(cancelVote.getErrorMsg());
            return responseCommonDto;
        }

        responseCommonDto.setSuccess(true);
        return responseCommonDto;
    }

    @Override
    public ResponseBlogListDto getMainPageBlogList() {
        int size = 3;
        User user = authUserService.getCurUser();
        List<BlogDto> blogDtoList;
        if(user == null){
            blogDtoList = getHotBlog(size);
        } else{
            //根据兴趣爱好找
            List<Long> typeIdList = preferenceService.getPreferenceTypeId(user.getId(), PreferenceTypeName.BLOG);
            List<Blog> blogList = getBlogListByTypeId(1, size, true, typeIdList);
            if(blogList.size() < size){
                List<Blog> blogListByNotTypeId = getBlogListByNotTypeId(1, size - blogList.size(), true, typeIdList);
                blogList.addAll(blogListByNotTypeId);
            }
            blogDtoList = assembleMainPageBlogListDto(blogList);
        }
        ResponseBlogListDto responseBlogListDto = new ResponseBlogListDto();
        responseBlogListDto.setPageInfo(new PageInfo(blogDtoList));
        responseBlogListDto.setSuccess(true);
        return responseBlogListDto;
    }

    @Override
    public List<BlogDto> getHotBlog(Integer size) {
        Page<Blog> page = PageHelper.startPage(1, size);
        BlogExample blogExample = new BlogExample();
        blogExample.setOrderByClause("vote_num desc");
        blogDao.selectByExample(blogExample);
        return assembleHotBlogListDto(page.getResult());
    }

    private List<Blog> getBlogList(int pageNum, int pageSize, boolean desc) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        BlogExample blogExample = assembleBlogExampleByModifyTimeDesc(desc);
        blogDao.selectByExample(blogExample);
        return page.getResult();
    }

    private List<Blog> getBlogListByTypeId(int pageNum, int pageSize, boolean desc, List<Long> typeIds) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        BlogExample blogExample = assembleBlogExampleByVoteNumDesc(desc);
        if(typeIds.size() > 0){
            blogExample.createCriteria().andBlogTypeIdIn(typeIds);
        }
        blogDao.selectByExample(blogExample);
        return page.getResult();
    }

    private List<Blog> getBlogListByNotTypeId(int pageNum, int pageSize, boolean desc, List<Long> typeIds) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        BlogExample blogExample = assembleBlogExampleByVoteNumDesc(desc);
        if(typeIds.size() > 0){
            blogExample.createCriteria().andBlogTypeIdNotIn(typeIds);
        }
        blogDao.selectByExample(blogExample);
        return page.getResult();
    }

    private List<Integer> getUserBlogExtraMsg(long userId){
        List<Integer> extraMsg = blogExtraMsgCache.getAll(userId);
        if(extraMsg == null){
            extraMsg = new ArrayList<>(4);
            List<Blog> blogs = getAllBlogByUserId(userId);
            Integer voteNum = 0, commentNum = 0;
            for(Blog blog : blogs){
                voteNum += blog.getVoteNum();
                commentNum += blog.getCommentNum();
            }
            extraMsg.add(blogs.size());
            extraMsg.add(getFansNum(userId));
            extraMsg.add(voteNum);
            extraMsg.add(commentNum);
            blogExtraMsgCache.addAll(userId, extraMsg);
        }
        return extraMsg;
    }

    /**
     * 获取用户所有blog
     * @param userId
     * @return
     */
    private List<Blog> getAllBlogByUserId(long userId){
        BlogExample blogExample = new BlogExample();
        BlogExample.Criteria criteria = blogExample.createCriteria();
        criteria.andUserIdEqualTo(userId);
        List<Blog> blogs = blogDao.selectByExample(blogExample);
        return blogs;
    }

    /**
     * 获取用户blog列表
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    private PageInfo getBlogListByUserId(long userId, int pageNum, int pageSize){
        Page page = PageHelper.startPage(pageNum, pageSize);
        getAllBlogByUserId(userId);
        PageInfo pageInfo = new PageInfo(page.getResult());
        return pageInfo;
    }

    private Integer getFansNum(long userId){
        List<Concern> fans = concernService.findFans(userId);
        return fans.size();
    }

    private BlogWithBLOBs BlogDto2do(BlogDto blogDto){
        BlogWithBLOBs blogWithBLOBs = dozerBeanMapper.map(blogDto, BlogWithBLOBs.class);
        return blogWithBLOBs;
    }

    private BlogDto BlogWithBLOBsDo2dto(BlogWithBLOBs blogWithBLOBs){
        BlogDto blogDto = dozerBeanMapper.map(blogWithBLOBs, BlogDto.class);
        return blogDto;
    }

    private BlogDto BlogDo2dto(Blog blog){
        BlogDto blogDto = dozerBeanMapper.map(blog, BlogDto.class);
        return blogDto;
    }

    private List<BlogDto> BlogDos2dto(List<Blog> blogs){
        List<BlogDto> blogDtos = new ArrayList<>(blogs.size());
        for(Blog blog : blogs){
            blogDtos.add(dozerBeanMapper.map(blog, BlogDto.class));
        }
        return blogDtos;
    }

    private Boolean isSelf(long curUserId, long blogUserId){
        if(curUserId == blogUserId){
            return true;
        }
        return false;
    }

    private ElasticsearchBlogDto BlogWithBLOBs2Elasticsearch(BlogWithBLOBs blogWithBLOBs){
        return dozerBeanMapper.map(blogWithBLOBs, ElasticsearchBlogDto.class);
    }

    private List<BlogDto> assembleMainPageBlogListDto(List<Blog> blogs) {
        return assembleHotBlogListDto(blogs);
    }

    private List<BlogDto> assembleHotBlogListDto(List<Blog> blogs) {
        List<BlogDto> blogDtos = new ArrayList<>(blogs.size());
        for(Blog blog : blogs){
            //删除不必要信息
            blog.setUserId(null);
            blog.setBlogTypeId(null);
            blog.setModifyTime(null);

            blogDtos.add(BlogDo2dto(blog));
        }
        return blogDtos;
    }

    private ResponseManagerBlogListDto assembleResponseManagerBlogListDto(PageInfo pageInfo){
        List<Blog> blogs = pageInfo.getList();
        List<ManagerBlogDto> managerBlogDtos = new ArrayList<>(blogs.size());
        for(Blog blog : blogs){
            ManagerBlogDto managerBlogDto = new ManagerBlogDto();
            managerBlogDtos.add(managerBlogDto);

            String title = blog.getTitle();
            if(title.length() > 20){
                title = title.substring(0, 20);
                title += "...";
            }

            User user = userService.findUserById(blog.getUserId());
            managerBlogDto.setBlogId(blog.getId());
            managerBlogDto.setBlogTitle(title);
            managerBlogDto.setUserNickname(user.getNickname());
            managerBlogDto.setUserAvatar(user.getAvatar());
        }
        pageInfo.setList(managerBlogDtos);

        ResponseManagerBlogListDto responseManagerBlogListDto = new ResponseManagerBlogListDto();
        responseManagerBlogListDto.setPageInfo(pageInfo);
        responseManagerBlogListDto.setSuccess(true);
        return responseManagerBlogListDto;
    }

    private BlogExample assembleBlogExampleByModifyTimeDesc(boolean desc){
        BlogExample blogExample = new BlogExample();
        if(desc){
            blogExample.setOrderByClause("modify_time desc");
        } else {
            blogExample.setOrderByClause("modify_time asc");
        }
        return blogExample;
    }

    private BlogExample assembleBlogExampleByVoteNumDesc(boolean desc){
        BlogExample blogExample = new BlogExample();
        if(desc){
            blogExample.setOrderByClause("vote_num desc");
        } else {
            blogExample.setOrderByClause("vote_num asc");
        }
        return blogExample;
    }

    private void addBlogTraffic(Long blogId, int num){
        lock.lock();
        try {
            BlogWithBLOBs blogWithBLOBs = blogDao.selectByPrimaryKey(blogId);
            blogWithBLOBs.setTraffic(blogWithBLOBs.getTraffic() + num);
            blogDao.updateByPrimaryKeySelective(blogWithBLOBs);
            elasticsearchService.updateBlog(BlogWithBLOBs2Elasticsearch(blogWithBLOBs));
        } catch (Exception e) {
            logger.error("addBlogTraffic:", e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 把<script...></script...> 改为<pre><div class="hljs"><code><code><script...></script...></code></code></div></pre>
     * 解决changeScriptStyle2标签内文本处于一行的情况，但是基于上传的标签是成对的
     * 如果上传的标签是成对的，这里就能很好的展示，如果不是成对的整个代码可能会乱掉
     * 所以先用changeScriptStyle解析一遍，为了避免changeScriptStyle漏掉的单个标签，再用changeScriptStyle2处理一遍
     * @param contentHtml
     * @return
     */
    private String changeScriptStyle(String contentHtml){
        String regex = "<(script[\\s\\S]*?)>([\\s\\S]*)<(/script.*)>|<(style[\\s\\S]*?)>([\\s\\S]*)<(/style.*)>|$";
        Pattern pattern = Pattern.compile(regex) ;
        Matcher matcher = pattern.matcher(contentHtml);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()){
            String oldStr1 = matcher.group(1);
            String oldStr2 = matcher.group(2);
            String oldStr3 = matcher.group(3);
            if(oldStr1 == null){
                oldStr1 = matcher.group(4);
                oldStr2 = matcher.group(5);
                oldStr3 = matcher.group(6);
            }
            //变成 <div class="hljs"><code>&lt;script ... &gt;</code></div>
            String newStr = "";
            if(oldStr1 != null){
                newStr = "<pre><div class=\"hljs\"><code>&lt;" + oldStr1 + "&gt;" + oldStr2 + "&lt;" + oldStr3 + "&gt;</code></div></pre>";
            }
            matcher.appendReplacement(sb, newStr);
        }
        String newContentHtml = sb.toString();
        if(newContentHtml.isEmpty()){
            //不存在嵌入script style
            newContentHtml = contentHtml;
        }
        return changeScriptStyle2(newContentHtml);
    }

    /**
     * 把<script...></script...> 改为<code><script...></code> <code></script...></code>
     * 这里是对单个标签进行处理，但是标签内的文本不能很好地展示，都会出现在一行
     * @param contentHtml
     * @return
     */
    private String changeScriptStyle2(String contentHtml){
        String regex = "<(/?script[\\s\\S]*?)>|<(/?style[\\s\\S]*?)>|$";
        Pattern pattern = Pattern.compile(regex) ;
        Matcher matcher = pattern.matcher(contentHtml);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()){
            String oldStr = matcher.group(1);
            if(oldStr == null){
                oldStr = matcher.group(2);
            }
            //变成 <div class="hljs"><code>&lt;script ... &gt;</code></div>
            String newStr = "";
            if(oldStr != null){
                newStr = "<code>&lt;" + oldStr + "&gt;</code>";
            }
            matcher.appendReplacement(sb, newStr);
        }
        String newContentHtml = sb.toString();
        if(newContentHtml.isEmpty()){
            //不存在嵌入script style
            newContentHtml = contentHtml;
        }
        return newContentHtml;
    }

    private int getUserBlogCount(Long userId){
        BlogExample blogExample = new BlogExample();
        blogExample.createCriteria().andUserIdEqualTo(userId);
        List<Blog> blogs = blogDao.selectByExample(blogExample);
        return blogs.size();
    }
}

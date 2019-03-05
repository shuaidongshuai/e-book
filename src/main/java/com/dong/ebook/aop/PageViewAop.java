package com.dong.ebook.aop;

import com.dong.ebook.dao.PageViewDao;
import com.dong.ebook.model.PageView;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Aspect
@Configuration
public class PageViewAop {
    private static Logger logger = Logger.getLogger(PageViewAop.class);
    private long pageViewNum = 0;
    private Lock lock = new ReentrantLock();

    @Autowired
    private PageViewDao pageViewDao;

    @Before("execution(* com.dong.ebook.controller.HomePageController.*(..))")
    public void homePageView(JoinPoint joinPoint){
        pageViewAdd();
    }

    @Before("execution(* com.dong.ebook.controller.BlogController.*(..))")
    public void blogPageView(JoinPoint joinPoint){
        pageViewAdd();
    }

    @Before("execution(* com.dong.ebook.controller.UserController.*(..))")
    public void userPageView(JoinPoint joinPoint){
        pageViewAdd();
    }

    @Before("execution(* com.dong.ebook.controller.SearchController.*(..))")
    public void searchPageView(JoinPoint joinPoint){
        pageViewAdd();
    }

    private void pageViewAdd(){
        lock.lock();
        try {
            ++pageViewNum;
        } catch (Exception e) {
            logger.error("pageViewAdd， pageViewNum=" + pageViewNum);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 每隔一个小时保存到数据库
     */
    @Async
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void autoSave(){
        Date date = new Date();
        PageView pageView = new PageView();
        pageView.setNumber(pageViewNum);
        pageView.setCreateTime(date);
        pageView.setModifyTime(date);
        pageViewDao.insertSelective(pageView);
        pageViewNum = 0;
    }
}

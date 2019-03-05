package com.dong.ebook.aop;

import com.dong.ebook.dao.HotWordsDao;
import com.dong.ebook.dao.HotWordsExtralDao;
import com.dong.ebook.model.HotWords;
import com.dong.ebook.model.HotWordsExample;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Aspect
@Configuration
public class HotWordsAop implements ApplicationRunner {
    private static Logger logger = Logger.getLogger(HotWordsAop.class);

    private Hashtable<String, Integer> hotWordstable = null;

    private static final int TABLESIZE = 10 * 1024;

    private static final int MAXQUERYSIZE = 128;

    private Lock lock = new ReentrantLock();

    @Autowired
    private HotWordsDao hotWordsDao;

    @Autowired
    private HotWordsExtralDao hotWordsExtralDao;

    @Override
    public void run(ApplicationArguments args) {
        initTable();
    }

    private void initTable(){
        //查数据库
        List<HotWords> hotWordsList = hotWordsDao.selectByExample(new HotWordsExample());
        hotWordstable = new Hashtable<>(hotWordsList.size());
        for(HotWords hotWords : hotWordsList){
            hotWordstable.put(hotWords.getWord(), hotWords.getNumber());
        }
    }

    @Before("execution(* com.dong.ebook.controller.SearchController.*(..))")
    public void hotWordsCount(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        String query = (String)args[0];
        if(query.length() > MAXQUERYSIZE){
            query.substring(0, MAXQUERYSIZE);
        }
        Integer number = hotWordstable.get(query);
        if(number != null){
            lock.lock();
            try {
                number += 1;
                hotWordstable.put(query, number);
            } catch (Exception e) {
                logger.error("hotWordsCount， query=" + query);
            } finally {
                lock.unlock();
            }
        }else{
            hotWordstable.put(query, 1);
        }
    }

    private void resetDatabaseHotWords(List<HotWords> hotWordsList){
        hotWordsExtralDao.deleteAll();
        hotWordsExtralDao.insertList(hotWordsList);
    }

    /**
     * 每隔一个小时保存到数据库
     */
    @Async
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void autoSave(){
        //先检查size
        int size = hotWordstable.size();
        long average = Long.MAX_VALUE;
        if(size > TABLESIZE){
            //计算平均值
            long sum = 0;
            for (Map.Entry<String, Integer> entry : hotWordstable.entrySet()) {
                sum += entry.getValue();
            }
            average = sum / size;
            size = TABLESIZE;
        }
        Date date = new Date();
        List<HotWords> hotWordsList = new ArrayList<>(size);
        for (Map.Entry<String, Integer> entry : hotWordstable.entrySet()) {
            //小于平均值的都舍去
            if(entry.getValue() < average){
                HotWords hotWords = new HotWords();
                hotWordsList.add(hotWords);

                hotWords.setWord(entry.getKey());
                hotWords.setNumber(entry.getValue());
                hotWords.setCreateTime(date);
                hotWords.setModifyTime(date);
            }
        }
        resetDatabaseHotWords(hotWordsList);
    }
}

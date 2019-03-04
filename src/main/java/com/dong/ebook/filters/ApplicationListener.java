package com.dong.ebook.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.HashMap;
import java.util.Map;

@WebListener
public class ApplicationListener implements ServletContextListener {
    private Logger logger = LoggerFactory.getLogger(ApplicationListener.class);

    private static final int MIN_USER_NUM = 1024;
    private static final int MIN_LIMIT_USER_NUM = 8;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("ApplicationListener 初始化成功");
        ServletContext context = sce.getServletContext();
        // IP存储器
        Map<String, Long[]> ipMap = new HashMap<>(MIN_USER_NUM);
        context.setAttribute("ipMap", ipMap);
        // 限制IP存储器：存储被限制的IP信息
        Map<String, Long> limitedIpMap = new HashMap<>(MIN_LIMIT_USER_NUM);
        context.setAttribute("limitedIpMap", limitedIpMap);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // TODO Auto-generated method stub
    }
}
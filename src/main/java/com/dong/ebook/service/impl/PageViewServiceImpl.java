package com.dong.ebook.service.impl;

import com.dong.ebook.dao.PageViewDao;
import com.dong.ebook.dto.PageViewDto;
import com.dong.ebook.dto.ResponsePageViewList;
import com.dong.ebook.model.PageView;
import com.dong.ebook.model.PageViewExample;
import com.dong.ebook.service.PageViewService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PageViewServiceImpl implements PageViewService {
    private static Logger logger = Logger.getLogger(PageViewServiceImpl.class);

    @Autowired
    private PageViewDao pageViewDao;

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @Override
    public ResponsePageViewList getPageView() {
        int size = 11;
        List<PageView> pageViews = getPageView(1, size, true);
        List<PageViewDto> pageViewDtos = new ArrayList<>(pageViews.size());
        for(PageView pageView : pageViews){
            pageViewDtos.add(do2dto(pageView));
        }
        ResponsePageViewList responsePageViewList = new ResponsePageViewList();
        responsePageViewList.setPageViewDtoList(pageViewDtos);
        responsePageViewList.setSuccess(true);
        return responsePageViewList;
    }

    private List<PageView> getPageView(int pageNum, int pageSize, boolean desc){
        Page page = PageHelper.startPage(pageNum, pageSize);
        PageViewExample pageViewExample = new PageViewExample();
        if(desc){
            pageViewExample.setOrderByClause("create_time desc");
        } else {
            pageViewExample.setOrderByClause("create_time asc");
        }
        pageViewDao.selectByExample(pageViewExample);
        return page.getResult();
    }

    private PageViewDto do2dto(PageView pageView){
        return dozerBeanMapper.map(pageView, PageViewDto.class);
    }
}

package com.dong.ebook.service.impl;

import com.dong.ebook.dao.HotWordsDao;
import com.dong.ebook.dto.HotWordsDto;
import com.dong.ebook.dto.ResponseHotWordsList;
import com.dong.ebook.model.HotWords;
import com.dong.ebook.model.HotWordsExample;
import com.dong.ebook.service.HotWordsService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HotWordsServiceImpl implements HotWordsService {
    private static Logger logger = Logger.getLogger(HotWordsServiceImpl.class);

    @Autowired
    private HotWordsDao hotWordsDao;

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @Override
    public ResponseHotWordsList getHotWords(Integer pageNum, Integer pageSize, Boolean desc) {
        if(pageNum == null || pageSize == null || desc == null){
            pageNum = 1;
            pageSize = 10;
            desc = true;
        }
        Page page = PageHelper.startPage(pageNum, pageSize);
        HotWordsExample hotWordsExample = new HotWordsExample();
        if(desc){
            hotWordsExample.setOrderByClause("number desc");
        } else {
            hotWordsExample.setOrderByClause("number asc");
        }
        hotWordsDao.selectByExample(hotWordsExample);
        PageInfo pageInfo = new PageInfo(page.getResult());

        ResponseHotWordsList responseHotWordsList = new ResponseHotWordsList();
        responseHotWordsList.setPageInfo(pageInfo);
        responseHotWordsList.setSuccess(true);
        return responseHotWordsList;
    }

    private HotWordsDto do2dto(HotWords hotWords){
        return dozerBeanMapper.map(hotWords, HotWordsDto.class);
    }
}

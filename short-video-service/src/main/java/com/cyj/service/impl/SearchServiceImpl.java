package com.cyj.service.impl;

import com.cyj.mapper.SearchRecordsMapper;
import com.cyj.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SearchRecordsMapper searchRecordsMapper;


    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<String> getHotWords() {
        return searchRecordsMapper.getHotWords();
    }
}

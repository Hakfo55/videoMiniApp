package com.cyj.mapper;

import com.cyj.pojo.SearchRecords;
import com.cyj.utils.MyMapper;

import java.util.List;

public interface SearchRecordsMapper extends MyMapper<SearchRecords> {
    public List<String> getHotWords();
}
package com.cyj.controller;

import com.cyj.service.SearchService;
import com.cyj.utils.VideoJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

@RestController
@RequestMapping("/search")
@Api(value = "搜索相关业务",tags = {"搜索相关业务"})
public class SearchController {
    @Autowired
    private SearchService searchService;

    @PostMapping("/hot")
    @ApiOperation(value = "展示热搜词",notes = "展示热搜词")
    public VideoJSONResult hot(){
        return VideoJSONResult.ok(searchService.getHotWords());
    }

}

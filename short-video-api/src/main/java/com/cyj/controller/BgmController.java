package com.cyj.controller;

import com.cyj.service.BgmService;
import com.cyj.utils.VideoJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "背景音乐业务接口",tags = {"背景音乐业务接口"})
@RestController
@RequestMapping("/bgm")
public class BgmController {
    @Autowired
    private BgmService bgmService;

    @ApiOperation(value = "获取背景音乐列表",notes = "获取背景音乐列表")
    @PostMapping("/list")
    public VideoJSONResult bgmList(){
        return VideoJSONResult.ok(bgmService.queryBgmList());
    }
}

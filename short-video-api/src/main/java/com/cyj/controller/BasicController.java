package com.cyj.controller;

import com.cyj.pojo.Users;
import com.cyj.service.UserService;
import com.cyj.utils.MD5Utils;
import com.cyj.utils.RedisOperator;
import com.cyj.utils.VideoJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicController {

	@Autowired
	public RedisOperator redis;

	public static final String USER_REDIS_SESSION = "user_redis_session";

	public static final String FILE_SPACE = "C:/Users/canyugin/Desktop/videoxcx/resources";

	public static final String FFMPEG_EXE = "C:\\Users\\canyugin\\Desktop\\ffmpeg\\bin\\ffmpeg.exe";
	
}

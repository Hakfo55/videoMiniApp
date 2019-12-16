package com.cyj.controller;

import com.cyj.pojo.Users;
import com.cyj.pojo.vo.UsersVo;
import com.cyj.service.UserService;
import com.cyj.utils.MD5Utils;
import com.cyj.utils.VideoJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Api(value = "用户注册登录登出的接口",tags = {"登录登出注册"})
public class RegistLoginController extends BasicController{
	@Autowired
	private UserService userService;

	public UsersVo setUserRedisSessionToken(Users user){
		String token = UUID.randomUUID().toString();
		redis.set(USER_REDIS_SESSION + ":" + user.getId(),token,7200);
		UsersVo usersVo = new UsersVo();
		BeanUtils.copyProperties(user,usersVo);
		usersVo.setUserToken(token);
		return usersVo;
	}

	@PostMapping("/regist")
	@ApiOperation(value = "用户注册",notes = "用户注册")
	public VideoJSONResult regist(@RequestBody Users user) {
		//1.判断用户名和密码不能为空
		if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())){
			return VideoJSONResult.errorMsg("用户名和密码不能为空");
		}
		//2.判断用户名是否存在在数据库了
		boolean usernameIsExist = userService.queryUserNameIsExist(user.getUsername());
		//3.保存用户注册信息到数据库
		if (!usernameIsExist){
			user.setNickname(user.getUsername());
			try {
				user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
			} catch (Exception e) {
				return VideoJSONResult.errorMsg(e.getMessage());
			}
			user.setFansCounts(0);
			user.setReceiveLikeCounts(0);
			user.setFollowCounts(0);
			userService.saveUser(user);
		}else{
			return VideoJSONResult.errorMsg("用户名已存在");
		}
		//由于返回给前端的时候不需要显示密码，那么我们可以在保存成功后对这个对象的密码设置为空，返回
		user.setPassword(null);

		//添加session
//		String token = UUID.randomUUID().toString();
//		redis.set(USER_REDIS_SESSION + ":" + user.getId(),token,7200);
//		UsersVo usersVo = new UsersVo();
//		BeanUtils.copyProperties(user,usersVo);
//		usersVo.setUserToken(token);

		UsersVo usersVo = setUserRedisSessionToken(user);
		return VideoJSONResult.ok(usersVo);
	}


	@PostMapping("/login")
	@ApiOperation(value = "用户登录",notes = "用户登录")
	public VideoJSONResult login(@RequestBody Users user) throws Exception {

//		Thread.sleep(3000);

		String username = user.getUsername();
		String password = user.getPassword();
		//1.判断用户名和密码不能为空
		if (StringUtils.isBlank(username) || StringUtils.isBlank(password)){
			return VideoJSONResult.errorMsg("用户名和密码不能为空！");
		}

		//2.判断用户是否存在
		boolean exist =  userService.queryUserNameIsExist(username);
		if (!exist){
			return VideoJSONResult.errorMsg("用户名不存在");
		}

		//3.判断密码正不正确
		Users result = userService.queryUserForLogin(username,MD5Utils.getMD5Str(password));

		//4.返回
		if (result != null){
			result.setPassword(null);
			UsersVo usersVo = setUserRedisSessionToken(result);
			return VideoJSONResult.ok(usersVo);
		}else {
			return VideoJSONResult.errorMsg("密码不正确");
		}
	}

	@ApiOperation(value = "用户注销",notes = "用户注销")
//	@ApiImplicitParam
	@PostMapping("/logout")
	public VideoJSONResult logout(@ApiParam(name = "userId",value = "用户Id",required = true) @RequestParam("userId") String userId){
		redis.del(USER_REDIS_SESSION + ":" + userId);
		return VideoJSONResult.ok();
	}

	
}

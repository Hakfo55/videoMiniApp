package com.cyj.controller;

import com.cyj.pojo.Users;
import com.cyj.pojo.UsersReport;
import com.cyj.pojo.Video;
import com.cyj.pojo.vo.PublisherVideoVo;
import com.cyj.pojo.vo.UsersVo;
import com.cyj.service.UserService;
import com.cyj.utils.MD5Utils;
import com.cyj.utils.VideoJSONResult;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

@RestController
@Api(value = "用户相关业务的接口",tags = {"用户相关业务"})
@RequestMapping("/user")
public class UserController extends BasicController{
	@Autowired
	private UserService userService;

	@PostMapping(value = "/uploadFace",headers = "content-type=multipart/form-data")
	@ApiOperation(value = "用户上传头像",notes = "用户上传头像")
    @ApiImplicitParam(name = "userId",value = "用户id",required = true,dataType = "String",paramType = "query")
    // TODO swagger要吃透，当参数有上传文件类型时，请求头需要content-type=multipart/form-data，然后参数不能用@RequestParam，直接去掉，然后给他个ApiParam注解
	public VideoJSONResult uploadFace(String userId,
                                      @ApiParam(value = "头像文件",required = true) MultipartFile files) throws Exception {

	    //判断userId
        if (StringUtils.isBlank(userId)){
            return VideoJSONResult.errorMsg("用户id不能为空");
        }

	    //文件保存的命名空间
//        String fileSpace = "C:/Users/canyugin/Desktop/videoxcx/resources";
        //保存到数据库中相对路径
        String uploadPathDB = "/" + userId + "/face";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
//            if (files != null && files.length>0){
            if (files != null){
                //获取文件名
//                String filename = files[0].getOriginalFilename();
                String filename = files.getOriginalFilename();
                if (StringUtils.isNotBlank(filename)){
                    //文件上传最终保存路径
                    String finalFacePath = FILE_SPACE + uploadPathDB + "/" + filename;

                    //设置数据库保存的相对路径
                    uploadPathDB += ("/" + filename);

                    File outFile = new File(finalFacePath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()){
                        //创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
//                    inputStream = files[0].getInputStream();
                    inputStream = files.getInputStream();
                    IOUtils.copy(inputStream,fileOutputStream);
                }
            }
            else {
                return VideoJSONResult.errorMsg("上传出错");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return VideoJSONResult.errorMsg("上传出错");
        }finally {
            if (fileOutputStream != null){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        Users user = new Users();
        user.setId(userId);
        user.setFaceImage(uploadPathDB);
        userService.updateUserInfo(user);

        return VideoJSONResult.ok(uploadPathDB);
    }


    @PostMapping("/query")
    @ApiOperation(value = "查询用户信息",notes = "查询用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户id",required = true,dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "fansId",value = "粉丝id",required = true,dataType = "String",paramType = "query")
    })

    public VideoJSONResult query(@RequestParam("userId") String userId,
                                 @RequestParam("fansId") String fansId){
        if (StringUtils.isBlank(userId)){
            return VideoJSONResult.errorMsg("用户id不能为空");
        }

        Users user = userService.queryUserInfo(userId);
        String token = redis.get(USER_REDIS_SESSION + ":" + userId);
        UsersVo usersVo = new UsersVo();
        BeanUtils.copyProperties(user,usersVo);
        usersVo.setUserToken(token);

        usersVo.setFollow(userService.queryIfFollow(userId,fansId));

        return VideoJSONResult.ok(usersVo);
    }

    @PostMapping("/queryPublisher")
    @ApiOperation(value = "查询视频发布者信息",notes = "查询视频发布者信息")
    public VideoJSONResult queryPublisher(
            @ApiParam(name = "loginUserId",value = "当前登录用户id",required = false)@RequestParam("loginUserId") String loginUserId,
            @ApiParam(name = "videoId",value = "视频id",required = false)@RequestParam("videoId") String videoId,
            @ApiParam(name = "publisherId",value = "视频发布者id",required = true)@RequestParam("publisherId") String publisherId){

        if (StringUtils.isBlank(publisherId)){
            return VideoJSONResult.errorMsg("");
        }

        // 1.查询视频发布者的信息
        Users user = userService.queryUserInfo(publisherId);
        UsersVo publisher = new UsersVo();
        BeanUtils.copyProperties(user,publisher);

        //2.查询当前登陆者和视频的点赞关系
        boolean userLikeVideo = userService.isUserLikeVideo(loginUserId,videoId);

        PublisherVideoVo publisherVideoVo = new PublisherVideoVo();
        publisherVideoVo.setPublisher(publisher);
        publisherVideoVo.setUserLikeVideo(userLikeVideo);

        return VideoJSONResult.ok(publisherVideoVo);

    }

    @PostMapping("/follow")
    @ApiOperation(value = "关注",notes = "关注")
    public VideoJSONResult follow(
            @ApiParam(name = "userId",value = "被关注者id",required = true) @RequestParam("userId") String userId,
            @ApiParam(name = "fansId",value = "粉丝id",required = true) @RequestParam("fansId")String fansId){

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fansId) ){
            return VideoJSONResult.errorMsg("");
        }
        userService.saveUserFanRelation(userId,fansId);
        return VideoJSONResult.ok("关注成功...");
    }

    @PostMapping("/unfollow")
    @ApiOperation(value = "取消关注",notes = "取消关注")
    public VideoJSONResult unfollow(String userId,String fansId){

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fansId) ){
            return VideoJSONResult.errorMsg("");
        }
        userService.delUserFanRelation(userId,fansId);
        return VideoJSONResult.ok("取消关注成功...");
    }

    @PostMapping("/reportUser")
    public VideoJSONResult reportUser(@RequestBody UsersReport usersReport){
        userService.report(usersReport);
        return VideoJSONResult.errorMsg("举报成功...");
    }
}

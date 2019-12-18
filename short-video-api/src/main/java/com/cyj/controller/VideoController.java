package com.cyj.controller;

import com.cyj.enums.VideoStatusEnum;
import com.cyj.pojo.Bgm;
import com.cyj.pojo.Comment;
import com.cyj.pojo.UsersReport;
import com.cyj.pojo.Video;
import com.cyj.service.BgmService;
import com.cyj.service.VideoService;
import com.cyj.utils.FetchVideoCover;
import com.cyj.utils.MergeVideoMp3;
import com.cyj.utils.PagedResult;
import com.cyj.utils.VideoJSONResult;
import groovy.util.IFileNameFinder;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.UUID;

@Api(value = "视频相关业务接口",tags = {"视频相关业务接口"})
@RestController
@RequestMapping("/video")
public class VideoController extends BasicController{

    @Autowired
    private BgmService bgmService;
    @Autowired
    private VideoService videoService;


    @ApiOperation(value = "上传视频",notes = "上传视频")
    @PostMapping(value = "/upload",headers = "content-type=multipart/form-data")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户ID",required = true,dataType = "String",paramType = "form"),
            @ApiImplicitParam(name = "bgmId",value = "背景音乐ID",required = false,dataType = "String",paramType = "form"),
            @ApiImplicitParam(name = "videoSeconds",value = "背景音乐长度",required = true,dataType = "String",paramType = "form"),
            @ApiImplicitParam(name = "videoWidth",value = "视频宽度",required = true,dataType = "String",paramType = "form"),
            @ApiImplicitParam(name = "videoHeight",value = "视频高度",required = true,dataType = "String",paramType = "form"),
            @ApiImplicitParam(name = "desc",value = "视频描述",required = false,dataType = "String",paramType = "form")
    })
    public VideoJSONResult upload(String userId, String bgmId,Double videoSeconds,
                                  Integer videoWidth, Integer videoHeight, String desc,
                                  @ApiParam(value = "短视频",required = true) MultipartFile file) throws IOException {
        if (StringUtils.isBlank(userId)){
            return VideoJSONResult.errorMsg("用户id不能为空");
        }

//        String fileSpace = "C:/Users/canyugin/Desktop/videoxcx/resources";

        String dbPath = "/" + userId + "/video";
        String coverPath = "/" + userId + "/video";

        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        String finalVideoPath = null;
        try {
            if (file!=null){
                String fileName = file.getOriginalFilename();
                //fileName是abcd.mp4,我们可以分割字符串，然后拼接一个abcd.jpg作为封面的文件名。需要对.号进行转义
                String fileNamePrefix = fileName.split("\\.")[0];

                if (StringUtils.isNotBlank(fileName)){
                    dbPath += ("/" + fileName);
                    coverPath += ("/" + fileNamePrefix + ".jpg");

                    finalVideoPath = FILE_SPACE + dbPath;
                    File outFile = new File(finalVideoPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()){
                        outFile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream,fileOutputStream);
                }
            }else {
                return VideoJSONResult.errorMsg("上传出错");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return VideoJSONResult.errorMsg("上传出错");
        }finally {
            if (fileOutputStream!=null){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        //判断bgmId是否为空，不为空
        //查询bgm信息，合并视频，生成新的视频，保存到数据库

        if (StringUtils.isNotBlank(bgmId)){
            Bgm bgm = bgmService.queryBgmById(bgmId);
            String mp3InputPath = FILE_SPACE + bgm.getPath();

            MergeVideoMp3 tool = new MergeVideoMp3(FFMPEG_EXE);
            String videoInputPath = finalVideoPath;
            String videoOutputName = UUID.randomUUID().toString() + ".mp4";
            dbPath = "/" + userId + "/video" + "/" + videoOutputName;
            finalVideoPath = FILE_SPACE + dbPath;
            tool.convertor(videoInputPath,mp3InputPath,videoSeconds,finalVideoPath);
        }

        System.out.println("uploadPathDB:" + dbPath);
        System.out.println("finalVideoPath:" + finalVideoPath);

        //对视频进行截图
        FetchVideoCover tool = new FetchVideoCover(FFMPEG_EXE);
        tool.getCover(finalVideoPath,FILE_SPACE + coverPath);

        //保存视频信息到数据库
        Video video = new Video();
        video.setUserId(userId);
        video.setAudioId(bgmId);
        video.setVideoSeconds(videoSeconds.floatValue());
        video.setVideoHeight(videoHeight);
        video.setVideoWidth(videoWidth);
        video.setVideoDesc(desc);
        video.setVideoPath(dbPath);
        video.setStatus(VideoStatusEnum.SUCCESS.getStatus());
        video.setCreateTime(new Date());

        video.setCoverPath(coverPath);
        String videoId = videoService.saveVideo(video);

        return VideoJSONResult.ok(videoId);
    }


    @ApiOperation(value = "上传视频封面",notes = "上传视频封面")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户Id",required = true,paramType = "form",dataType = "String"),
            @ApiImplicitParam(name = "videoId",value = "视频Id",required = true,paramType = "form",dataType = "String")
    })
    @PostMapping(value = "/uploadCover",headers = "content-type=multipart/form-data")
    public VideoJSONResult uploadCover(String userId,String videoId,
                                       @ApiParam(value = "视频封面",required = true) MultipartFile file) throws IOException {
        if (StringUtils.isBlank(videoId) || StringUtils.isBlank(userId)){
            return VideoJSONResult.errorMsg("视频id和用户id不能为空");
        }

        String dbPath = "/" + userId + "/video";
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        String finalCoverPath = null;
        try {
            if (file!=null){
                String fileName = file.getOriginalFilename();
                dbPath += ("/" + fileName);
                finalCoverPath = FILE_SPACE + dbPath;

                File outFile = new File(finalCoverPath);
                if(outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()){
                    outFile.getParentFile().mkdirs();
                }

                fileOutputStream = new FileOutputStream(finalCoverPath);
                inputStream = file.getInputStream();
                IOUtils.copy(inputStream,fileOutputStream);
            }else{
                return VideoJSONResult.errorMsg("上传封面出错");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return VideoJSONResult.errorMsg("上传封面出错");
        }finally {
            if (fileOutputStream!=null){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        return VideoJSONResult.ok();
    }



    @PostMapping("/showAll")
    @ApiOperation(value = "查询视频列表",notes = "查询视频列表")
    public VideoJSONResult showAll(
            @RequestBody Video video,
            @ApiParam(name = "isSaveRecord",value = "1保存,0不保存",defaultValue = "1") @RequestParam(name ="isSaveRecord",required = false) Integer isSaveRecord,
            @ApiParam(name = "page",value = "第几页",required = true,defaultValue = "1") @RequestParam(name = "page",defaultValue = "1") Integer page,
            @ApiParam(name = "pageSize",value = "每页显示多少内容",required = true,defaultValue = "1") @RequestParam(name = "pageSize",defaultValue = "5") Integer pageSize){

        PagedResult pagedResult = videoService.getAllVideos(video,isSaveRecord,page,pageSize);
        return VideoJSONResult.ok(pagedResult);

    }

    @PostMapping(value = "/userLike")
    @ApiOperation(value = "用户点赞视频",notes = "用户点赞视频")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户id",required = true,paramType = "query",dataType = "String"),
            @ApiImplicitParam(name = "videoId",value = "视频id",required = true,paramType = "query",dataType = "String"),
            @ApiImplicitParam(name = "videoCreaterId",value = "视频发布者id",required = true,paramType = "query",dataType = "String")
    })
    public VideoJSONResult userLike(
            @RequestParam("userId") String userId,
            @RequestParam("videoId") String videoId,
            @RequestParam("videoCreaterId") String videoCreaterId
    ){
        videoService.userLikeVideo(userId, videoId, videoCreaterId);
        return VideoJSONResult.ok();
    }

    @PostMapping(value = "/userUnLike")
    @ApiOperation(value = "用户取消点赞视频",notes = "用户取消点赞视频")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户id",required = true,paramType = "query",dataType = "String"),
            @ApiImplicitParam(name = "videoId",value = "视频id",required = true,paramType = "query",dataType = "String"),
            @ApiImplicitParam(name = "videoCreaterId",value = "视频发布者id",required = true,paramType = "query",dataType = "String")
    })
    public VideoJSONResult userUnLike(
            @RequestParam("userId") String userId,
            @RequestParam("videoId") String videoId,
            @RequestParam("videoCreaterId") String videoCreaterId
    ){
        videoService.userUnLikeVideo(userId, videoId, videoCreaterId);
        return VideoJSONResult.ok();
    }

    @PostMapping("/showMyLike")
    public VideoJSONResult showMyLike(
            @RequestParam(name ="userId") String userId,
            @RequestParam(name = "page",defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize",defaultValue = "6") Integer pageSize){

        if (StringUtils.isBlank(userId)){
            return VideoJSONResult.errorMsg("");
        }

        PagedResult pagedResult = videoService.queryMyLikeVideo(userId,page,pageSize);
        return VideoJSONResult.ok(pagedResult);
    }

    @PostMapping("/showMyFollow")
    public VideoJSONResult showMyFollow(
            @RequestParam(name ="userId") String userId,
            @RequestParam(name = "page",defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize",defaultValue = "6") Integer pageSize){

        if (StringUtils.isBlank(userId)){
            return VideoJSONResult.errorMsg("");
        }

        PagedResult pagedResult = videoService.queryMyFollowVideo(userId,page,pageSize);
        return VideoJSONResult.ok(pagedResult);
    }

    @PostMapping("/saveComment")
    public VideoJSONResult saveComment(@RequestBody Comment comment){
        videoService.saveComment(comment);
        return VideoJSONResult.ok();
    }

    @PostMapping("/getVideoComments")
    public VideoJSONResult getVideoComments(
            @RequestParam(name ="videoId") String videoId,
            @RequestParam(name ="page",defaultValue = "1") Integer page,
            @RequestParam(name ="pageSize",defaultValue = "10") Integer pageSize){
        if (StringUtils.isBlank(videoId)){
            return VideoJSONResult.errorMsg("视频id为空");
        }
        PagedResult result = videoService.getVideoComments(videoId, page, pageSize);
        return VideoJSONResult.ok(result);
    }

}

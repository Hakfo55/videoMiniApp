package com.cyj.service;

import com.cyj.pojo.Comment;
import com.cyj.pojo.Video;
import com.cyj.utils.PagedResult;

import java.util.List;

public interface VideoService {

    String saveVideo(Video video);

    void updateVideo(String videoId,String coverPath);

    /**
     * 分页查询视频列表
     * @param page 查询的页数
     * @param pageSize 每一页显示多少内容
     * @return
     */
    PagedResult getAllVideos(Video video,Integer isSaveRecord,Integer page,Integer pageSize);

    void userLikeVideo(String userId,String videoId,String videoCreaterId);

    void userUnLikeVideo(String userId,String videoId,String videoCreaterId);

    PagedResult queryMyLikeVideo(String userId,Integer page,Integer pageSize);

    PagedResult queryMyFollowVideo(String userId,Integer page,Integer pageSize);

    void saveComment(Comment comment);

    PagedResult getVideoComments(String videoId,Integer page,Integer pageSize);
}

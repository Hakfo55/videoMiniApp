package com.cyj.mapper;

import com.cyj.pojo.Video;
import com.cyj.pojo.vo.VideoVo;
import com.cyj.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VideoMapper extends MyMapper<Video> {
    List<VideoVo> queryAllVideos(@Param("videoDesc") String videoDesc,
                                 @Param("userId") String userId);

    void addVideoLikeCount(String videoId);

    void reduceVideoLikeCount(String videoId);

    List<VideoVo> queryMyLikeVideos(@Param("userId") String userId);

    List<VideoVo> queryMyFollowVideos(@Param("userId") String userId);
}
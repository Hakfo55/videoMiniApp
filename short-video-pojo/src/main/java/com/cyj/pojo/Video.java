package com.cyj.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

@Data
@ApiModel(value = "视频对象",description = "视频对象")
public class Video {
    @Id
    @ApiModelProperty(hidden = true)
    private String id;

//    @ApiModelProperty(hidden = true)
    @ApiModelProperty(name = "vuserId",value = "用户id",example = "191115ANNNKSFFW0")
    @Column(name = "user_id")
    private String userId;

    @ApiModelProperty(hidden = true)
    @Column(name = "audio_id")
    private String audioId;

    @ApiModelProperty(name = "videoDesc",value = "视频描述",example = "跳舞")
    @Column(name = "video_desc")
    private String videoDesc;

    @ApiModelProperty(hidden = true)
    @Column(name = "video_path")
    private String videoPath;

    @ApiModelProperty(hidden = true)
    @Column(name = "video_seconds")
    private Float videoSeconds;

    @ApiModelProperty(hidden = true)
    @Column(name = "video_height")
    private Integer videoHeight;

    @ApiModelProperty(hidden = true)
    @Column(name = "video_width")
    private Integer videoWidth;

    @ApiModelProperty(hidden = true)
    @Column(name = "cover_path")
    private String coverPath;

    @ApiModelProperty(hidden = true)
    @Column(name = "like_counts")
    private Long likeCounts;

    /**
     * 视频状态：1、发布成功，2、禁止播放，管理员进行操作
     */
    @ApiModelProperty(hidden = true)
    private Integer status;

    @ApiModelProperty(hidden = true)
    @Column(name = "create_time")
    private Date createTime;

}
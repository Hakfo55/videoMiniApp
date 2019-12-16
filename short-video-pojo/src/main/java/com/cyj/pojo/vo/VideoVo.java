package com.cyj.pojo.vo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

@Data
public class VideoVo {
    private String id;
    private String userId;
    private String audioId;
    private String videoDesc;
    private String videoPath;
    private Float videoSeconds;
    private Integer videoHeight;
    private Integer videoWidth;
    private String coverPath;
    private Long likeCounts;
    private Integer status;
    private Date createTime;

    private String nickname;
    private String faceImage;


}
package com.cyj.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
@ApiModel(value = "用户对象",description = "这是用户对象")
public class Users {
    @Id
    @ApiModelProperty(hidden = true)
    private String id;

    @ApiModelProperty(value = "用户名",name = "username",required = true,example = "cyj")
    private String username;

    @ApiModelProperty(value = "密码",name = "password",required = true,example = "123456")
    private String password;

    @Column(name = "face_image")
    @ApiModelProperty(hidden = true)
    private String faceImage;

    private String nickname;

    @Column(name = "fans_counts")
    @ApiModelProperty(hidden = true)
    private Integer fansCounts;

    @Column(name = "follow_counts")
    @ApiModelProperty(hidden = true)
    private Integer followCounts;

    @Column(name = "receive_like_counts")
    @ApiModelProperty(hidden = true)
    private Integer receiveLikeCounts;

}
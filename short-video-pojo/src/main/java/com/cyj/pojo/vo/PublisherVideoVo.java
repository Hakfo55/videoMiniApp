package com.cyj.pojo.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PublisherVideoVo {
   private UsersVo publisher;
   private boolean userLikeVideo;

}
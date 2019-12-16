package com.cyj.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "users_like_videos")
@Data
public class UsersLikeVideos {
    @Id
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "video_id")
    private String videoId;

}
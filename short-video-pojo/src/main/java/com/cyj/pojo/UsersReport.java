package com.cyj.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "users_report")
@Data
public class UsersReport {
    @Id
    private String id;

    @Column(name = "deal_user_id")
    private String dealUserId;

    @Column(name = "deal_video_id")
    private String dealVideoId;

    private String title;

    private String content;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "create_date")
    private Date createDate;

}
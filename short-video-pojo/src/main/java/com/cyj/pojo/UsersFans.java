package com.cyj.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "users_fans")
@Data
public class UsersFans {
    @Id
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "fan_id")
    private String fanId;

}
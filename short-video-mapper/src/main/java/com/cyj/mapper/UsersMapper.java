package com.cyj.mapper;

import com.cyj.pojo.Users;
import com.cyj.utils.MyMapper;

public interface UsersMapper extends MyMapper<Users> {


    void addReceiveLikeCount(String userId);

    void reduceReceiveLikeCount(String userId);

    //增加粉丝数
    void addFansCount(String userId);
    //减少粉丝数
    void reduceFansCount(String userId);
    //增加关注数
    void addFollersCount(String userId);
    //减少粉丝数
    void reduceFollersCount(String userId);
}
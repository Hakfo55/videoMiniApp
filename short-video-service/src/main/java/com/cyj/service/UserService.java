package com.cyj.service;

import com.cyj.pojo.Users;
import com.cyj.pojo.UsersReport;

public interface UserService {
    void saveUser(Users user);

    boolean queryUserNameIsExist(String username);
    
    Users queryUserForLogin(String username,String password);

    void updateUserInfo(Users user);

    Users queryUserInfo(String userId);

    boolean isUserLikeVideo(String userId,String videoId);

    //视频发布者和当前用户的关系，关注/粉丝
    void saveUserFanRelation(String userId,String fansId);

    //删除视频发布者和当前用户的关系，取消关注/粉丝
    void delUserFanRelation(String userId,String fansId);

    //查询是否关注
    boolean queryIfFollow(String userId,String fansId);

    //举报
    void report(UsersReport usersReport);
}

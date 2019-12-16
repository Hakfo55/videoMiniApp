package com.cyj.service.impl;

import com.cyj.mapper.UsersFansMapper;
import com.cyj.mapper.UsersLikeVideosMapper;
import com.cyj.mapper.UsersMapper;
import com.cyj.mapper.UsersReportMapper;
import com.cyj.pojo.Users;
import com.cyj.pojo.UsersFans;
import com.cyj.pojo.UsersLikeVideos;
import com.cyj.pojo.UsersReport;
import com.cyj.service.UserService;
import com.cyj.utils.VideoJSONResult;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;
    @Autowired
    private UsersFansMapper usersFansMapper;
    @Autowired
    private UsersReportMapper usersReportMapper;

    @Autowired
    private Sid sid;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUser(Users user) {
        user.setId(sid.nextShort());
        usersMapper.insert(user);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean queryUserNameIsExist(String username) {
        Users user = new Users();
        user.setUsername(username);
        Users result = usersMapper.selectOne(user);
        return result == null ? false : true;
    }

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Users queryUserForLogin(String username, String password) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username",username);
        criteria.andEqualTo("password",password);
        Users result = usersMapper.selectOneByExample(example);
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserInfo(Users user) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",user.getId());
        usersMapper.updateByExampleSelective(user,example);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Users queryUserInfo(String userId) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",userId);
        return usersMapper.selectOneByExample(example);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isUserLikeVideo(String userId, String videoId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(videoId) ){
            return false;
        }

        Example example = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("videoId",videoId);
        List<UsersLikeVideos> result = usersLikeVideosMapper.selectByExample(example);
        if (result !=null && result.size()>0){
            return true;
        }
        return false;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUserFanRelation(String userId, String fansId) {
        //1.添加关联表信息
        String id = sid.nextShort();
        UsersFans usersFans = new UsersFans();
        usersFans.setId(id);
        usersFans.setUserId(userId);
        usersFans.setFanId(fansId);
        usersFansMapper.insert(usersFans);

        //2.更新用户表
        usersMapper.addFansCount(userId);
        usersMapper.addFollersCount(fansId);

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delUserFanRelation(String userId, String fansId) {
        //1.删除关联表信息
        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("fanId",fansId);
        usersFansMapper.deleteByExample(example);

        //2.更新用户表
        usersMapper.reduceFansCount(userId);
        usersMapper.reduceFollersCount(fansId);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean queryIfFollow(String userId, String fansId) {
        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("fanId",fansId);

//        List<UsersFans> list = usersFansMapper.selectByExample(example);
//        if (list != null && !list.isEmpty() && list.size()>0){
//            return true;
//        }
//        return false;

        UsersFans result = usersFansMapper.selectOneByExample(example);
        return result!=null?true:false;


    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void report(UsersReport usersReport) {
        usersReport.setId(sid.nextShort());
        usersReport.setCreateDate(new Date());
        usersReportMapper.insert(usersReport);
    }


}

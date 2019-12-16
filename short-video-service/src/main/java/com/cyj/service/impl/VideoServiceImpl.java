package com.cyj.service.impl;

import com.cyj.mapper.SearchRecordsMapper;
import com.cyj.mapper.UsersLikeVideosMapper;
import com.cyj.mapper.UsersMapper;
import com.cyj.mapper.VideoMapper;
import com.cyj.pojo.SearchRecords;
import com.cyj.pojo.UsersLikeVideos;
import com.cyj.pojo.Video;
import com.cyj.pojo.vo.VideoVo;
import com.cyj.service.VideoService;
import com.cyj.utils.PagedResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private Sid sid;
    @Autowired
    private SearchRecordsMapper searchRecordsMapper;
    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;
    @Autowired
    private UsersMapper usersMapper;


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String saveVideo(Video video) {
        String id = sid.nextShort();
        video.setId(id);
        videoMapper.insertSelective(video);
        return id;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateVideo(String videoId, String coverPath) {
        Video video = new Video();
        video.setId(videoId);
        video.setCoverPath(coverPath);
        videoMapper.updateByPrimaryKeySelective(video);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED) //涉及到增删改需要换成REQUIRED，查询的话可以只弄SUPPORT
    public PagedResult getAllVideos(Video video,Integer isSaveRecord,Integer page, Integer pageSize) {

        //保存热搜词
        String videoDesc = video.getVideoDesc();

        //用户id
        String userId = video.getUserId();

        //1表示要保存到热搜词数据库表
        if (isSaveRecord != null && isSaveRecord == 1) {
            SearchRecords record = new SearchRecords();
            String recordId = sid.nextShort();
            record.setId(recordId);
            record.setContent(videoDesc);
            searchRecordsMapper.insert(record);
        }

        PageHelper.startPage(page,pageSize);
        List<VideoVo> videoVoList = videoMapper.queryAllVideos(videoDesc,userId);
        PageInfo<VideoVo> pageInfo = new PageInfo<>(videoVoList);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageInfo.getPages());
        pagedResult.setRows(videoVoList);
        pagedResult.setRecords(pageInfo.getTotal());

        return pagedResult;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void userLikeVideo(String userId, String videoId, String videoCreaterId) {
        //1.保存用户和视频的喜欢点赞数据到关联关系表
        String likeId = sid.nextShort();
        UsersLikeVideos usersLikeVideos = new UsersLikeVideos();
        usersLikeVideos.setId(likeId);
        usersLikeVideos.setVideoId(videoId);
        usersLikeVideos.setUserId(userId);
        usersLikeVideosMapper.insert(usersLikeVideos);

        //对video进行字段修改，视频喜欢数量累加
        videoMapper.addVideoLikeCount(videoId);

        //对user进行字段修改，用户受欢迎数量累加
        usersMapper.addReceiveLikeCount(videoCreaterId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void userUnLikeVideo(String userId, String videoId, String videoCreaterId) {
        //1.删除用户和视频的喜欢点赞数据到关联关系表
        Example example = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("videoId",videoId);
        usersLikeVideosMapper.deleteByExample(example);

        //对video进行字段修改，视频喜欢数量累减
        videoMapper.reduceVideoLikeCount(videoId);

        //对user进行字段修改，用户受欢迎数量累减
        usersMapper.reduceReceiveLikeCount(videoCreaterId);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PagedResult queryMyLikeVideo(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        List<VideoVo> videoVoList = videoMapper.queryMyLikeVideos(userId);

        PageInfo<VideoVo> pageInfo = new PageInfo<>(videoVoList);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageInfo.getPages());
        pagedResult.setRows(videoVoList);
        pagedResult.setRecords(pageInfo.getTotal());

        return pagedResult;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PagedResult queryMyFollowVideo(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        List<VideoVo> list = videoMapper.queryMyFollowVideos(userId);

        PageInfo<VideoVo> result = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setRows(list);
        pagedResult.setRecords(result.getTotal());
        pagedResult.setTotal(result.getPages());

        return pagedResult;

    }

}

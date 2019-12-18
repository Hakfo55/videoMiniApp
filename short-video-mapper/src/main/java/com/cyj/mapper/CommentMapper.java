package com.cyj.mapper;

import com.cyj.pojo.Comment;
import com.cyj.pojo.vo.CommentVO;
import com.cyj.utils.MyMapper;

import java.util.List;

public interface CommentMapper extends MyMapper<Comment> {
    List<CommentVO> findByVideoId(String videoId);
}
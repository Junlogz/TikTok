package com.zjl.mapper;

import com.zjl.vo.IndexVlogVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface VlogMapperCustom {

    /**
     * 查询首页/搜索vlog列表
     * @param map
     * @return
     */
    List<IndexVlogVO> getIndexVlogList(@Param("paramMap") Map<String, Object> map);

    /**
     * 根据视频主键查询vlog
     * @param map
     * @return
     */
    List<IndexVlogVO> getVlogDetailById(@Param("paramMap")Map<String, Object> map);

    /**
     * 查询用户点赞过的短视频 对应赞过那里的列表
     * @param map
     * @return
     */
    List<IndexVlogVO> getMyLikedVlogList(@Param("paramMap")Map<String, Object> map);

    /**
     * 我关注的视频列表 就是关注页刷出来的视频
     * @param map
     * @return
     */
    List<IndexVlogVO> getMyFollowVlogList(@Param("paramMap")Map<String, Object> map);

    /**
     * 我的朋友视频列表
     * @param map
     * @return
     */
    List<IndexVlogVO> getMyFriendVlogList(@Param("paramMap")Map<String, Object> map);

}
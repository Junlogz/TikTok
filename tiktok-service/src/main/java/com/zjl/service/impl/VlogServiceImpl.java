package com.zjl.service.impl;

import com.github.pagehelper.PageHelper;
import com.zjl.base.BaseInfoProperties;
import com.zjl.bo.VlogBO;
import com.zjl.enums.YesOrNo;
import com.zjl.mapper.MyLikedVlogMapper;
import com.zjl.mapper.VlogMapper;
import com.zjl.mapper.VlogMapperCustom;
import com.zjl.pojo.MyLikedVlog;
import com.zjl.pojo.Vlog;
import com.zjl.service.VlogService;
import com.zjl.util.PagedGridResult;
import com.zjl.vo.IndexVlogVO;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: JunLog
 * @Description: *
 * Date: 2022/8/9 18:13
 */
@Service
public class VlogServiceImpl extends BaseInfoProperties implements VlogService {

    @Autowired
    private VlogMapper vlogMapper;

    @Autowired
    private VlogMapperCustom vlogMapperCustom;

    @Autowired
    private MyLikedVlogMapper myLikedVlogMapper;

    @Autowired
    private Sid sid;

    /**
     * 新增vlog
     * @param vlogBO
     */
    @Transactional
    @Override
    public void createVlog(VlogBO vlogBO) {

        String vid = sid.nextShort();

        Vlog vlog = new Vlog();
        BeanUtils.copyProperties(vlogBO, vlog);

        vlog.setId(vid);

        vlog.setLikeCounts(0);
        vlog.setCommentsCounts(0);
        vlog.setIsPrivate(YesOrNo.NO.type);

        vlog.setCreatedTime(new Date());
        vlog.setUpdatedTime(new Date());

        vlogMapper.insert(vlog);
    }

    /**
     * 查询首页/搜索的vlog列表
     * @param userId
     * @param search
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public PagedGridResult getIndexVlogList(String userId, String search , Integer page, Integer pageSize) {

        PageHelper.startPage(page, pageSize);

        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotBlank(search)) {
            map.put("search", search);
        }

        List<IndexVlogVO> list = vlogMapperCustom.getIndexVlogList(map);

        for (IndexVlogVO v : list) {
            String vlogerId = v.getVlogerId();
            String vlogId = v.getVlogId();

            // 判断是否点赞视频
            if (StringUtils.isNotBlank(userId)) {
                // 就能直接拿到用户是否点赞视频
                v.setDoILikeThisVlog(doILikeVlog(userId, vlogId));
            }

            // 获得当前视频被点赞过的总数
            v.setLikeCounts(getVlogBeLikedCounts(vlogId));
        }

        return setterPagedGrid(list, page);
    }

    /**
     * 获取视频点赞视频总数 根据vlogId从redis中取
     * @param vlogId
     * @return
     */
    @Override
    public Integer getVlogBeLikedCounts(String vlogId) {
        String countsStr = redis.get(REDIS_VLOG_BE_LIKED_COUNTS + ":" + vlogId);
        if (StringUtils.isBlank(countsStr)) {
            countsStr = "0";
        }
        return Integer.valueOf(countsStr);
    }

    /**
     * 查询是否是点过赞的视频 直接从redis里面拿 给getIndexVlogList调用
     * @param myId
     * @param vlogId
     * @return
     */
    private boolean doILikeVlog(String myId, String vlogId) {

        String doILike = redis.get(REDIS_USER_LIKE_VLOG + ":" + myId + ":" + vlogId);
        boolean isLike = false;
        if (StringUtils.isNotBlank(doILike) && doILike.equalsIgnoreCase("1")) {
            isLike = true;
        }
        return isLike;
    }


    /**
     * 根据视频主键查询vlog
     * @param vlogId
     * @return
     */
    @Override
    public IndexVlogVO getVlogDetailById(String vlogId) {

        Map<String, Object> map = new HashMap<>();
        map.put("vlogId", vlogId);

        List<IndexVlogVO> list = vlogMapperCustom.getVlogDetailById(map);

        // 对list判空
        if (list != null && list.size() > 0 && !list.isEmpty()) {
            IndexVlogVO vlogVO = list.get(0);
            return vlogVO;
        }

        return null;
    }

    /**
     * 用户把视频改为公开/私密的视频
     * @param userId
     * @param vlogId
     * @param yesOrNo
     */
    @Override
    public void changeToPrivateOrPublic(String userId,
                                        String vlogId,
                                        Integer yesOrNo) {
        Example example = new Example(Vlog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", vlogId);
        criteria.andEqualTo("vlogerId", userId);

        Vlog pendingVlog = new Vlog();
        pendingVlog.setIsPrivate(yesOrNo);

        vlogMapper.updateByExampleSelective(pendingVlog, example);
    }

    /**
     * 查询用户的公开/私密的视频列表
     * @param userId
     * @param page
     * @param pageSize
     * @param yesOrNo
     * @return
     */
    @Override
    public PagedGridResult queryMyVlogList(String userId,
                                           Integer page,
                                           Integer pageSize,
                                           Integer yesOrNo) {
        Example example = new Example(Vlog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("vlogerId", userId);
        criteria.andEqualTo("isPrivate", yesOrNo);

        PageHelper.startPage(page, pageSize);
        List<Vlog> list = vlogMapper.selectByExample(example);

        return setterPagedGrid(list, page);
    }

    /**
     * 用户点赞/喜欢视频
     * @param userId
     * @param vlogId
     */
    @Transactional
    @Override
    public void userLikeVlog(String userId, String vlogId) {

        String rid = sid.nextShort();

        MyLikedVlog likedVlog = new MyLikedVlog();
        likedVlog.setId(rid);
        likedVlog.setVlogId(vlogId);
        likedVlog.setUserId(userId);

        myLikedVlogMapper.insert(likedVlog);

    }

    /**
     * 用户取消点赞/喜欢视频
     * @param userId
     * @param vlogId
     */
    @Transactional
    @Override
    public void userUnLikeVlog(String userId, String vlogId) {

        MyLikedVlog likedVlog = new MyLikedVlog();
        likedVlog.setVlogId(vlogId);
        likedVlog.setUserId(userId);

        myLikedVlogMapper.delete(likedVlog);
    }

}

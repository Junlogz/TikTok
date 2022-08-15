package com.zjl.service.impl;

import com.github.pagehelper.PageHelper;
import com.zjl.base.BaseInfoProperties;
import com.zjl.bo.VlogBO;
import com.zjl.enums.MessageEnum;
import com.zjl.enums.YesOrNo;
import com.zjl.mapper.FansMapper;
import com.zjl.mapper.FansMapperCustom;
import com.zjl.mapper.VlogMapper;
import com.zjl.mapper.VlogMapperCustom;
import com.zjl.pojo.Fans;
import com.zjl.pojo.Vlog;
import com.zjl.service.FansService;
import com.zjl.service.VlogService;
import com.zjl.util.PagedGridResult;
import com.zjl.vo.FansVO;
import com.zjl.vo.IndexVlogVO;
import com.zjl.vo.VlogerVO;
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
public class FansServiceImpl extends BaseInfoProperties implements FansService {

    @Autowired
    private FansMapper fansMapper;

    @Autowired
    private FansMapperCustom fansMapperCustom;

    @Autowired
    private Sid sid;

    @Transactional
    @Override
    public void doFollow(String myId, String vlogerId) {

        String fid = sid.nextShort();

        Fans fans = new Fans();
        fans.setId(fid);
        fans.setFanId(myId);
        fans.setVlogerId(vlogerId);

        // 判断对方是否关注我，如果关注我，那么双方都要互为朋友关系
        // 这里需要myId, vlogerId反过来 因为需要去查对方是否关注自己
        Fans vloger = queryFansRelationship(vlogerId, myId);
        if (vloger != null) {
            // 双方的IsFanFriendOfMine都要设置为yes也就是1
            fans.setIsFanFriendOfMine(YesOrNo.YES.type);

            // 双方的IsFanFriendOfMine都要设置为yes也就是1
            vloger.setIsFanFriendOfMine(YesOrNo.YES.type);
            // 更新
            fansMapper.updateByPrimaryKeySelective(vloger);
        } else {
            fans.setIsFanFriendOfMine(YesOrNo.NO.type);
        }

        fansMapper.insert(fans);

        // 系统消息：关注
//        msgService.createMsg(myId, vlogerId, MessageEnum.FOLLOW_YOU.type, null);
    }

    @Override
    public void doCancel(String myId, String vlogerId) {

        // 判断我们是否朋友关系，如果是，则需要取消双方的关系
        Fans fan = queryFansRelationship(myId, vlogerId);
        if (fan != null && fan.getIsFanFriendOfMine() == YesOrNo.YES.type) {
            // 抹除双方的朋友关系，自己的关系删除即可
            // 查询对方与自己的粉丝关系
            Fans pendingFan = queryFansRelationship(vlogerId, myId);
            // 将is_fan_friend_of_min置为0
            pendingFan.setIsFanFriendOfMine(YesOrNo.NO.type);
            // 更新
            fansMapper.updateByPrimaryKeySelective(pendingFan);
        }

        // 删除自己的关注关联表记录
        fansMapper.delete(fan);

    }

    // 查询用户是否关注博主
    @Override
    public boolean queryDoIFollowVloger(String myId, String vlogerId) {
        Fans vloger = queryFansRelationship(myId, vlogerId);
        return vloger != null;
    }

    // 查询关注的博主列表
    @Override
    public PagedGridResult queryMyFollows(String myId,
                                          Integer page,
                                          Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("myId", myId);

        PageHelper.startPage(page, pageSize);

        List<VlogerVO> list = fansMapperCustom.queryMyFollows(map);

        return setterPagedGrid(list, page);
    }

    // 查询我的粉丝列表
    @Override
    public PagedGridResult queryMyFans(String myId, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("myId", myId);

        PageHelper.startPage(page, pageSize);

        List<FansVO> list = fansMapperCustom.queryMyFans(map);

        // 通过查询出来的粉丝集合 再进行遍历 从redis里面获取前面存储的互粉关系 如果为设置的1 那就是互粉关系 就setFriend true
        for (FansVO f : list) {
            String relationship = redis.get(REDIS_FANS_AND_VLOGGER_RELATIONSHIP + ":" + myId + ":" + f.getFanId());
            if (StringUtils.isNotBlank(relationship) && relationship.equalsIgnoreCase("1")){
                f.setFriend(true);
            }
        }

        return setterPagedGrid(list, page);
    }

    // 查询互粉关系 根据eq-fanId和vlogerId 去做条件查询 先查询一下是否互相关注了
    public Fans queryFansRelationship(String fanId, String vlogerId) {
        Example example = new Example(Fans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("vlogerId", vlogerId);
        criteria.andEqualTo("fanId", fanId);

        List list =  fansMapper.selectByExample(example);

        Fans fan = null;
        if (list != null && list.size() > 0 && !list.isEmpty()) {
            fan = (Fans)list.get(0);
        }
        return fan;
    }
}

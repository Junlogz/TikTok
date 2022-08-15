package com.zjl.service;

import com.zjl.util.PagedGridResult;

/**
 * @author: JunLog
 * @Description: *
 * Date: 2022/8/11 18:50
 */
public interface FansService {

    /**
     * 关注
     */
    public void doFollow(String myId, String vlogerId);

    /**
     * 取关
     */
    public void doCancel(String myId, String vlogerId);

    /**
     * 查询用户是否关注博主
     */
    public boolean queryDoIFollowVloger(String myId, String vlogerId);

    /**
     * 查询我关注的博主列表
     */
    public PagedGridResult queryMyFollows(String myId,
                                          Integer page,
                                          Integer pageSize);

    /**
     * 查询我的粉丝列表
     */
    public PagedGridResult queryMyFans(String myId,
                                       Integer page,
                                       Integer pageSize);

}

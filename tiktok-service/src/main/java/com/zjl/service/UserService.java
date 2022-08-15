package com.zjl.service;

import com.zjl.bo.UpdatedUserBO;
import com.zjl.pojo.Users;

/**
 * @author: JunLog
 * @Description: *
 * Date: 2022/8/7 15:02
 */
public interface UserService {

    /**
     * 判断用户是否存在，如果存在则返回用户信息
     */
    public Users queryMobileIsExist(String mobile);

    /**
     * 创建用户信息，并且返回用户对象
     */
    public Users createUser(String mobile);

    /**
     * 根据用户主键查询用户信息
     */
    public Users getUser(String userId);

    /**
     * 用户信息修改
     */
    public Users updateUserInfo(UpdatedUserBO updatedUserBO);

    /**
     * 用户信息修改
     */
    public Users updateUserInfo(UpdatedUserBO updatedUserBO, Integer type);

}

package com.zjl.service.impl;

import com.zjl.bo.UpdatedUserBO;
import com.zjl.enums.Sex;
import com.zjl.enums.UserInfoModifyType;
import com.zjl.enums.YesOrNo;
import com.zjl.exceptions.GraceException;
import com.zjl.grace.result.ResponseStatusEnum;
import com.zjl.mapper.UsersMapper;
import com.zjl.pojo.Users;
import com.zjl.service.UserService;
import com.zjl.util.DateUtil;
import com.zjl.util.DesensitizationUtil;
import org.checkerframework.checker.units.qual.A;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

/**
 * @author: JunLog
 * @Description: *
 * Date: 2022/8/7 15:03
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    private static final String USER_FACE1 = "http://122.152.205.72:88/group1/M00/00/05/CpoxxF6ZUySASMbOAABBAXhjY0Y649.png";

    @Override
    public Users queryMobileIsExist(String mobile) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("mobile", mobile);
        Users user = usersMapper.selectOneByExample(userExample);
        return user;
    }

    @Transactional
    @Override
    public Users createUser(String mobile) {

        // 获得全局唯一主键
        String userId = sid.nextShort();

        Users user = new Users();
        user.setId(userId);

        user.setMobile(mobile);
        user.setNickname("用户：" + DesensitizationUtil.commonDisplay(mobile));
        user.setImoocNum("用户：" + DesensitizationUtil.commonDisplay(mobile));
        user.setFace(USER_FACE1);

        user.setBirthday(DateUtil.stringToDate("1900-01-01"));
        user.setSex(Sex.secret.type);

        user.setCountry("中国");
        user.setProvince("");
        user.setCity("");
        user.setDistrict("");
        user.setDescription("这家伙很懒，什么都没留下~");
        user.setCanImoocNumBeUpdated(YesOrNo.YES.type);

        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        usersMapper.insert(user);

        return user;
    }

    @Override
    public Users getUser(String userId) {
        return usersMapper.selectByPrimaryKey(userId);
    }

    @Transactional
    @Override
    public Users updateUserInfo(UpdatedUserBO updatedUserBO) {

        Users pendingUser = new Users();
        BeanUtils.copyProperties(updatedUserBO, pendingUser);

        int result = usersMapper.updateByPrimaryKeySelective(pendingUser);
        if (result != 1) {
            GraceException.display(ResponseStatusEnum.USER_UPDATE_ERROR);
        }

        return getUser(updatedUserBO.getId());
    }

    @Transactional
    @Override
    public Users updateUserInfo(UpdatedUserBO updatedUserBO, Integer type) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        // 判断昵称是否重复
        if (type == UserInfoModifyType.NICKNAME.type) {
            criteria.andEqualTo("nickname", updatedUserBO.getNickname());
            Users user = usersMapper.selectOneByExample(example);
            if (user != null) {
                GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_NICKNAME_EXIST_ERROR);
            }
        }

        // 判断号是否重复 以及看是否能修改
        if (type == UserInfoModifyType.IMOOCNUM.type) {
            criteria.andEqualTo("imoocNum", updatedUserBO.getImoocNum());
            Users user = usersMapper.selectOneByExample(example);
            // 昵称已存在
            if (user != null) {
                GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_NICKNAME_EXIST_ERROR);
            }

            Users tempUser = getUser(updatedUserBO.getId());
            // 为no无法修改
            if (tempUser.getCanImoocNumBeUpdated() == YesOrNo.NO.type) {
                GraceException.display(ResponseStatusEnum.USER_INFO_CANT_UPDATED_IMOOCNUM_ERROR);
            }

            updatedUserBO.setCanImoocNumBeUpdated(YesOrNo.NO.type);
        }

        return updateUserInfo(updatedUserBO);
    }
}

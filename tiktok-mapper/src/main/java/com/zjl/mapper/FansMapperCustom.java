package com.zjl.mapper;

import com.zjl.my.mapper.MyMapper;
import com.zjl.pojo.Fans;
import com.zjl.vo.FansVO;
import com.zjl.vo.VlogerVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FansMapperCustom extends MyMapper<Fans> {

    public List<VlogerVO> queryMyFollows(@Param("paramMap") Map<String, Object> map);

    public List<FansVO> queryMyFans(@Param("paramMap") Map<String, Object> map);
}
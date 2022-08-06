package com.zjl.controller;

import com.zjl.util.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: JunLog
 * @Description: *
 * Date: 2022/8/5 21:19
 */
public class BaseInfoProperties {

    @Autowired
    public RedisOperator redis;

    public static final String MOBILE_SMSCODE = "mobile:smscode";
    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final String REDIS_USER_INFO = "redis_user_info";

//        public Map<String, String> getErrors(BindingResult result) {
//        Map<String, String> map = new HashMap<>();
//        List<FieldError> errorList = result.getFieldErrors();
//        for (FieldError ff : errorList) {
//            // 错误所对应的属性字段名
//            String field = ff.getField();
//            // 错误的信息
//            String msg = ff.getDefaultMessage();
//            map.put(field, msg);
//        }
//        return map;
//    }

}

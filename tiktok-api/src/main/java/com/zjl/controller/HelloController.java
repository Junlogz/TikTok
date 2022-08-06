package com.zjl.controller;

import com.zjl.grace.result.GraceJSONResult;
import com.zjl.util.SMSUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: JunLog
 * @Description: *
 * Date: 2022/8/3 22:14
 */
@Slf4j
@Api(tags = "Hello 测试的接口")
@RestController
public class HelloController {

    @Autowired
    private SMSUtils smsUtils;

    @GetMapping("/hello")
    public Object hello() {
        return GraceJSONResult.ok("hello world");
    }


    @GetMapping("/sms")
    public Object sms() throws Exception{
        String code = "123456";
        smsUtils.sendSMS("13423735633",code);
        return GraceJSONResult.ok();
    }

}

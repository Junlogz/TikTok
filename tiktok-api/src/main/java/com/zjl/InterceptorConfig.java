package com.zjl;

import com.zjl.intercepter.PassportInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器的注册 为写好的PassportInterceptor注册拦截
 */

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public PassportInterceptor passportInterceptor() {
        return new PassportInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportInterceptor())
                .addPathPatterns("/passport/getSMSCode");

    }
}

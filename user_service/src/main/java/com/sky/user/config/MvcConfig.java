package com.sky.user.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import common.stp.util.StpEmployeeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class MvcConfig implements WebMvcConfigurer {
    // 注册 Sa-Token 拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {

            /*// 如果这个接口，要求客户端登录了后台 Admin 账号才能访问：
            SaRouter.match("/art/getInfo").check(r -> StpUtil.checkLogin());*/

            // 如果这个接口，要求客户端登录了前台 Employee 账号才能访问：
            SaRouter.match("/admin/**").notMatch("/admin/employee/login").check(r -> StpEmployeeUtil.checkLogin());
/*
            // 如果这个接口，要求客户端同时登录 Admin 和 User 账号，才能访问：
            SaRouter.match("/art/getInfo").check(r -> {
                StpUtil.checkLogin();
                StpUserUtil.checkLogin();
            });

            // 如果这个接口，要求客户端登录 Admin 和 User 账号任意一个，就能访问：
            SaRouter.match("/art/getInfo").check(r -> {
                if(StpUtil.isLogin() == false && StpUserUtil.isLogin() == false) {
                    throw new SaTokenException("请登录后再访问接口");
                }
            });*/

        })).addPathPatterns("/**");
    }

}

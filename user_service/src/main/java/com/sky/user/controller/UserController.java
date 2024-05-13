package com.sky.user.controller;

import common.result.SkyResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static common.result.SkyResult.success;

/**
 * 用户端
 */
@RestController
@RequestMapping("/user/user")
@Slf4j
public class UserController {
    /**
     * 用户微信登录
     * @param code 微信授权码
     * @return id:服务器用户id,openid:微信平台用户id,token：授权码
     */
    public SkyResult<String> login(String code){
        return SkyResult.success();
    }



}

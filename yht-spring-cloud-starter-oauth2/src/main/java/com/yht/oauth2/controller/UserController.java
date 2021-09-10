package com.yht.oauth2.controller;

import com.yht.common.api.CommonResult;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 鱼仔
 * @date 2021/9/8 2:29 下午
 * 概要
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping("/getCurrentUser")
    public CommonResult getCurrentUser(Authentication authentication) {
        return CommonResult.success(authentication);
    }
}

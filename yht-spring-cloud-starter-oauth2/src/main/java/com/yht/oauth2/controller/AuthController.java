package com.yht.oauth2.controller;

import com.alibaba.druid.util.StringUtils;
import com.yht.common.api.CommonResult;
import com.yht.common.constant.AuthConstant;
import com.yht.common.entity.Oauth2TokenDTO;
import com.yht.common.exception.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 鱼仔
 * @date 2021/9/7 4:07 下午
 * 概要
 */
@Api(tags = "AuthController", description = "认证中心登录认证")
@RestController
@RequestMapping(value = "oauth")
public class AuthController {
    @Resource
    TokenEndpoint tokenEndpoint;


    @ApiOperation("GET用户密码获取token")
    @GetMapping("/token")
    public CommonResult getAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        return custom(tokenEndpoint.getAccessToken(principal, parameters).getBody());
    }

    @ApiOperation("POST用户密码获取token")
    @PostMapping("/token")
    public CommonResult postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        // 获取客户端ID
        String clientId = parameters.get("client_id");
        if (StringUtils.isEmpty(clientId)) {
            throw new ApiException("客户端ID不能为空");
        }
        return custom(tokenEndpoint.postAccessToken(principal, parameters).getBody());
    }

    /**
     * 封装返回参数
     *
     * @param accessToken
     * @return
     */
    private CommonResult custom(OAuth2AccessToken accessToken) {
        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
        Map<String, Object> data = new LinkedHashMap(token.getAdditionalInformation());
        data.put("accessToken", token.getValue());
        data.put("tokenHead", AuthConstant.JWT_TOKEN_PREFIX);
        if (token.getRefreshToken() != null) {
            data.put("refreshToken", token.getRefreshToken().getValue());
        }
        return CommonResult.success(data);
    }
}

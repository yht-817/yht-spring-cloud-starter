package com.yht.oauth2.error;

import com.yht.common.api.CommonResult;
import com.yht.common.constant.MessageConstant;
import com.yht.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局处理Oauth2抛出的异常
 *
 * @author 鱼仔
 */
@ControllerAdvice
@Slf4j
public class Oauth2ExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value = OAuth2Exception.class)
    public CommonResult handleOauth2(OAuth2Exception e) {
        if ("Bad credentials".equals(e.getMessage())) {
            return CommonResult.failed(MessageConstant.USERNAME_PASSWORD_ERROR);
        }
        return CommonResult.failed(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = ApiException.class)
    public CommonResult handle(ApiException e) {
        if (e.getErrorCode() != null) {
            return CommonResult.failed(e.getErrorCode());
        }
        return CommonResult.failed(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = InternalAuthenticationServiceException.class)
    public CommonResult handleOauth2(InternalAuthenticationServiceException e) {
        return CommonResult.failed(e.getMessage());
    }
}
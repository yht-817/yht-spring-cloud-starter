package com.yht.oauth2.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yht.common.api.CommonResult;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AuthenticationEntryPoint是Spring Security Web一个概念模型接口，顾名思义，他所建模的概念是:“认证入口点”。
 * 它在用户请求处理过程中遇到认证异常时，被ExceptionTranslationFilter用于开启特定认证方案(authentication schema)的认证流程。
 * <p>
 * 这里参数request是遇到了认证异常authException用户请求，response是将要返回给客户的相应，方法commence实现,也就是相应的认证方案逻辑会修改response并返回给用户引导用户进入认证流程。
 *
 * @author 鱼仔
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        e.printStackTrace();
        response.setStatus(200);
        CommonResult result = CommonResult.failed(e.getMessage());
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        response.getWriter().print(objectMapper.writeValueAsString(result));
        response.getWriter().flush();
    }

}

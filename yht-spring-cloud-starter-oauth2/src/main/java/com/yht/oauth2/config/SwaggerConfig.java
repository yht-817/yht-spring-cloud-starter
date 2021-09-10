package com.yht.oauth2.config;

import com.yht.common.config.BaseSwaggerConfig;
import com.yht.common.entity.SwaggerProperties;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger API文档相关配置
 * Created by 鱼仔 on 2018/4/26.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig extends BaseSwaggerConfig {

    @Override
    public SwaggerProperties swaggerProperties() {
        return SwaggerProperties.builder()
                .apiBasePackage("com.yht.oauth2.controller")
                .title("用户认证中心")
                .description("用户认证中心相关接口")
                .contactName("yht")
                .version("1.0")
                .enableSecurity(true)
                .build();
    }
}

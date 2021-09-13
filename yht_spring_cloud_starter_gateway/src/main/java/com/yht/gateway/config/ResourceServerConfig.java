package com.yht.gateway.config;

import cn.hutool.core.util.ArrayUtil;
import com.yht.common.constant.AuthConstant;
import com.yht.gateway.authorization.AuthorizationManager;
import com.yht.gateway.component.RestAuthenticationEntryPoint;
import com.yht.gateway.component.RestfulAccessDeniedHandler;
import com.yht.gateway.filter.IgnoreUrlsRemoveJwtFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 网关对安全配置策略配置，资源服务器配置
 *
 * @author 鱼仔
 */
@AllArgsConstructor
@Configuration
@EnableWebFluxSecurity
public class ResourceServerConfig {
    /**
     * 鉴权管理器，判断用户是否有权限访问
     */
    private final AuthorizationManager authorizationManager;
    /**
     * 白名单路径
     */
    private final IgnoreUrlsConfig ignoreUrlsConfig;
    /**
     * 自定义返回结果：没有权限访问时
     */
    private final RestfulAccessDeniedHandler restfulAccessDeniedHandler;
    /**
     * 自定义返回结果：没有登录或token过期时
     */
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    /**
     * 对白名单路径，直接移除JWT请求头
     */
    private final IgnoreUrlsRemoveJwtFilter ignoreUrlsRemoveJwtFilter;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.oauth2ResourceServer().jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter());
        //自定义处理JWT请求头过期或签名错误的结果
        http.oauth2ResourceServer().authenticationEntryPoint(restAuthenticationEntryPoint);
        //对白名单路径，直接移除JWT请求头
        http.addFilterBefore(ignoreUrlsRemoveJwtFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        http.authorizeExchange()
                //白名单配置
                .pathMatchers(ArrayUtil.toArray(ignoreUrlsConfig.getUrls(), String.class)).permitAll()
                //鉴权管理器配置
                .anyExchange().access(authorizationManager)
                .and().exceptionHandling()
                //处理未授权
                .accessDeniedHandler(restfulAccessDeniedHandler)
                //处理未认证
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and().csrf().disable();
        return http.build();
    }

    /**
     * 根据token然后去获取用户信息和角色
     *
     * @return
     */
    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(AuthConstant.AUTHORITY_PREFIX);
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(AuthConstant.AUTHORITY_CLAIM_NAME);
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

}

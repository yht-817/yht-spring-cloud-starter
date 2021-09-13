package com.yht.oauth2.config;

import com.yht.common.constant.AuthConstants;
import com.yht.oauth2.granter.PhonePasswordAbstractTokenGranter;
import com.yht.oauth2.model.UserInfoModel;
import com.yht.oauth2.service.impl.PhonePasswordServiceImpl;
import com.yht.oauth2.service.impl.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.security.KeyPair;
import java.util.*;

/**
 * 认证服务器配置
 * <p>
 *
 * @author 鱼仔
 * @date 2020/6/19
 */
@AllArgsConstructor
@Configuration
@EnableAuthorizationServer
public class Oauth2ServerConfig extends AuthorizationServerConfigurerAdapter {
    /**
     * 认证提供者的
     */
    @Resource
    private final AuthenticationManager authenticationManager;

    /**
     * 加密
     */
    @Resource
    private final PasswordEncoder passwordEncoder;

    /**
     * 配置Oauth2的数据源
     */
    @Resource
    private DataSource dataSource;

    /**
     * redis的数据源
     */
    @Resource
    private RedisConnectionFactory connectionFactory;

    /**
     * 查询用户信息
     *
     * @return
     */
    @Resource
    private UserServiceImpl userService;

    @Resource
    protected PhonePasswordServiceImpl phonePasswordService;

    @Bean
    public ClientDetailsService clientDetails() {
        // 基于 JDBC 实现，需要事先在数据库配置客户端信息，从数据库读取客户端配置信息
        return new JdbcClientDetailsService(dataSource);
    }

    @Bean
    public TokenStore jdbcTokenStore() {
        // 基于 JDBC 实现，令牌保存到数据
        return new JdbcTokenStore(dataSource);
    }

    @Bean
    public TokenStore redisTokenStore() {
        // 基于 redis 实现，令牌保存到数据
        return new RedisTokenStore(connectionFactory);
    }


    /**
     * 定义客户端详细信息服务的配置器
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        /*clients.inMemory() //内存模式
                .withClient("admin-app")
                .secret(passwordEncoder.encode("123456"))
                .scopes("all")
                .authorizedGrantTypes("password", "refresh_token")
                .accessTokenValiditySeconds(3600 * 24)
                .refreshTokenValiditySeconds(3600 * 24 * 7);*/
        // jdbc数据库模式，数据库存的密钥是要根据PasswordEncoder加密后的字符串
        clients.withClientDetails(clientDetails());
    }

    /**
     * 定义授权和令牌端点以及令牌服务。
     *
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenGranter> tokenGranters = getTokenGranters(endpoints.getAuthorizationCodeServices(), endpoints.getTokenStore(), endpoints.getTokenServices(), endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory());

        List<TokenEnhancer> tokenEnhancers = new ArrayList<>();
        tokenEnhancers.add(tokenEnhancer());
        tokenEnhancers.add(accessTokenConverter());
        //配置JWT的内容增强器
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);
        endpoints.authenticationManager(authenticationManager)
                // 配置加载用户信息的服务
                .userDetailsService(userService)
                .accessTokenConverter(accessTokenConverter())
                .tokenEnhancer(tokenEnhancerChain)
                .tokenGranter(new CompositeTokenGranter(tokenGranters))
                // 把用户token数据保存到数据库
                // redis或者数据库（jwt可以不需要存入数据库中）
//                .tokenStore(jdbcTokenStore())
                /*refresh_token有两种使用方式：重复使用(true)、非重复使用(false)，默认为true
                     1.重复使用：access_token过期刷新时， refresh token过期时间未改变，仍以初次生成的时间为准
                     2.非重复使用：access_token过期刷新时， refresh_token过期时间延续，在refresh_token有效期内刷新而无需失效再次登录*/
                .reuseRefreshTokens(true);
    }

    /**
     * 定义令牌端点上的安全约束。
     * <p>
     * 下面配置支持将client和client_secret参数放在header或body中
     * 允许表单认证和Basic Auth(Authorization)两个提交方式
     *
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        // 允许表单认证
        security.allowFormAuthenticationForClients();
        // 开启/oauth/check_token验证端口认证权限访问，isAuthenticated()不允许匿名访问
        security.tokenKeyAccess("isAuthenticated()")
                // 开启/oauth/token_key验证端口无权限访问，permitAll()允许匿名访问
                .checkTokenAccess("permitAll()");
    }

    /**
     * 访问令牌转换器
     *
     * @return
     */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setKeyPair(keyPair());
        return jwtAccessTokenConverter;
    }

    /**
     * JWT内容增强
     */
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (accessToken, authentication) -> {
            Map<String, Object> map = new HashMap<>();
            boolean isClientOnly = authentication.isClientOnly();
            if (!isClientOnly) {// 判断是否只是客户端模式
                UserInfoModel userInfoModel = (UserInfoModel) authentication.getUserAuthentication().getPrincipal();
                map.put(AuthConstants.JWT_USER_ID_KEY, userInfoModel.getId());
                map.put(AuthConstants.JWT_CLIENT_ID_KEY, userInfoModel.getClientId());
            } else {
                map.put(AuthConstants.JWT_CLIENT_ID_KEY, authentication.getPrincipal());
            }
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(map);
            return accessToken;
        };
    }

    /**
     * 把自定义的授权方式加入令牌生产
     *
     * @param authorizationCodeServices
     * @param tokenStore
     * @param tokenServices
     * @param clientDetailsService
     * @param requestFactory
     * @return
     */
    private List<TokenGranter> getTokenGranters(AuthorizationCodeServices authorizationCodeServices, TokenStore tokenStore, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        List<TokenGranter> tokenGranters = new ArrayList();
        //授权码模式
        tokenGranters.add(new AuthorizationCodeTokenGranter(tokenServices, authorizationCodeServices, clientDetailsService, requestFactory));
        //refresh模式
        tokenGranters.add(new RefreshTokenGranter(tokenServices, clientDetailsService, requestFactory));
        //简化模式
        ImplicitTokenGranter implicit = new ImplicitTokenGranter(tokenServices, clientDetailsService, requestFactory);
        tokenGranters.add(implicit);
        //客户端模式
        tokenGranters.add(new ClientCredentialsTokenGranter(tokenServices, clientDetailsService, requestFactory));
        if (authenticationManager != null) {
            //密码模式
            tokenGranters.add(new ResourceOwnerPasswordTokenGranter(authenticationManager, tokenServices, clientDetailsService, requestFactory));
            //自定义手机密码模式
            tokenGranters.add(new PhonePasswordAbstractTokenGranter(phonePasswordService, tokenServices, clientDetailsService, requestFactory));
        }
        return tokenGranters;
    }

    @Bean
    public KeyPair keyPair() {
        //从classpath下的证书中获取秘钥对
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "123456".toCharArray());
        return keyStoreKeyFactory.getKeyPair("jwt", "123456".toCharArray());
    }
}

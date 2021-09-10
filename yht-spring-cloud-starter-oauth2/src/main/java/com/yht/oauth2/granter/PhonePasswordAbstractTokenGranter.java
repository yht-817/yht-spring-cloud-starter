package com.yht.oauth2.granter;

import com.yht.oauth2.model.UserInfoModel;
import com.yht.oauth2.service.impl.PhonePasswordServiceImpl;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.Map;

/**
 * @author 鱼仔
 * @date 2021/9/9 4:41 下午
 * 自定义手机号码密码登陆
 */
public class PhonePasswordAbstractTokenGranter extends AbstractCustomTokenGranter {

    private static final String GRANT_TYPE = "phone_password";

    protected PhonePasswordServiceImpl phonePasswordService;

    public PhonePasswordAbstractTokenGranter(PhonePasswordServiceImpl phonePasswordService, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.phonePasswordService = phonePasswordService;
    }

    @Override
    protected UserInfoModel getUserInfo(Map<String, String> parameters) {
        String phone = parameters.get("phone");
        String password = parameters.get("password");
        return phonePasswordService.loadUserByMobileAndPassword(phone, password);
    }
}

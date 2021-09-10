package com.yht.oauth2.service.impl;

import com.yht.common.constant.MessageConstant;
import com.yht.oauth2.dao.UserServiceDAO;
import com.yht.oauth2.model.UserInfoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 鱼仔
 * @date 2021/9/7 5:27 下午
 * 概要
 */
@Service
public class UserServiceImpl implements UserDetailsService {
    @Resource
    private HttpServletRequest request;

    @Resource
    private UserServiceDAO userServiceDAO;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String clientId = request.getParameter("client_id");
        UserInfoModel userInfoModel = userServiceDAO.loadUserByUsername(username);
        if (userInfoModel == null) {
            throw new UsernameNotFoundException(MessageConstant.USERNAME_PASSWORD_ERROR);
        }
        if (userInfoModel.getStatus() == 1) {
            throw new DisabledException(MessageConstant.ACCOUNT_LOCKED);
        }
        List<String> roles = userServiceDAO.getRoles(userInfoModel.getId());
        userInfoModel.setClientId(clientId);
        userInfoModel.setAuthorities(roles);
        return userInfoModel;
    }
}

package com.yht.common.entity;

import lombok.Data;

import java.util.List;

/**
 * 登录用户信息
 *
 * @author 鱼仔
 * @date 2020/6/19
 */
@Data
public class UserInfoDTO {
    /**
     * ID
     */
    private Long id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 用户状态
     */
    private Integer status;
    /**
     * 登录客户端ID
     */
    private String clientId;
    /**
     * 权限数据
     */
    private List<String> authorities;

    public UserInfoDTO() {

    }
}

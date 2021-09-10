package com.yht.common.entity;

import cn.hutool.json.JSONUtil;

import java.util.List;

/**
 * @author 鱼仔
 * @date 2021/6/11 1:49 下午
 * 概要
 */
public class RoleResources {
    private String url;
    private List<String> roleIds;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setRoleIds(String roleIds) {
        this.roleIds = JSONUtil.parseArray(roleIds).toList(String.class);
    }

    public String getUrl() {
        return url;
    }

    public List<String> getRoleIds() {
        return roleIds;
    }

    @Override
    public String toString() {
        return "RoleResources{" +
                "url='" + url + '\'' +
                ", roleIds=" + roleIds +
                '}';
    }
}

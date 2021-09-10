package com.yht.oauth2.dao;

import com.yht.common.entity.RoleResources;
import com.yht.oauth2.model.UserInfoModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 鱼仔
 * @date 2021/9/7 4:43 下午
 * 概要
 */
@Mapper
public interface PhonePasswordServiceDAO {
    UserInfoModel loadUserByUsername(@Param("userName") String userName);

    List<String> getRoles(@Param("id") Long id);

    List<RoleResources> getRoleList();
}

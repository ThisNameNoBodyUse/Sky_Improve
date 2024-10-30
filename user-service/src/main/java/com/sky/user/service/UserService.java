package com.sky.user.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

import java.util.Map;

public interface UserService {

    /**
     * 微信登陆
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);

    /**
     * 根据用户id查询用户
     * @param userId
     * @return
     */
    User getById(Long userId);

    /**
     * 根据动态条件统计用户数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}

package com.sky.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;

import com.sky.properties.WeChatProperties;

import com.sky.user.mapper.UserMapper;
import com.sky.user.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    //微信服务接口地址
    private static final String WX_LOGIN = "http://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    /**
     * 微信登录
     *
     * @param userLoginDTO
     * @return
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {

        String openid = getOpenid(userLoginDTO.getCode());
        //判断openid是否为空，如果为空则登录数百，抛出业务异常
        log.info("openid:{}", openid);
        if (openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);//登陆失败
        }

        //判断当前用户是否为新用户
        User user = userMapper.getByOpenid(openid);

        //如果是新用户，自动完成注册
        if (user == null) {
            user = User.builder().openid(openid).createTime(LocalDateTime.now()).build();
            userMapper.insert(user);
        }

        //返回这个用户对象
        return user;
    }

    /**
     * 根据id查询用户
     * @param userId
     * @return
     */
    @Override
    public User getById(Long userId) {
        return userMapper.getById(userId);
    }

    /**
     * 根据动态条件统计用户数量
     * @param map
     * @return
     */
    @Override
    public Integer countByMap(Map map) {
        return userMapper.countByMap(map);
    }

    /**
     * 调用微信接口服务，获取当前微信用户的openid
     *
     * @param code
     * @return
     */
    private String getOpenid(String code) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");

        log.info("map:{}", JSON.toJSONString(map));
        String json = HttpClientUtil.doGet(WX_LOGIN, map);

        JSONObject jsonObject = JSON.parseObject(json);

        log.info("json:{}", jsonObject.toJSONString());
        String openid = jsonObject.getString("openid");
        return openid;

    }
}

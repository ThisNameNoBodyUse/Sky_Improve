package com.sky.user.controller.inner;

import com.sky.entity.User;
import com.sky.user.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController("innerUser")
@RequestMapping("/user")
@Api(tags = "内部用户接口")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId) {
        return userService.getById(userId);
    }

    @GetMapping("/total")
    public Integer countByMap(@RequestParam Map map) {
        return userService.countByMap(map);
    }


}

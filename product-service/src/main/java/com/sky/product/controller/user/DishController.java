package com.sky.product.controller.user;


import com.sky.constant.StatusConstant;

import com.sky.entity.Dish;
import com.sky.product.service.DishService;
import com.sky.result.Result;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Api(tags = "菜品浏览接口")
@Slf4j
public class DishController {


    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询菜品及口味
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品及口味")
    public Result<List<DishVO>> list(Long categoryId) {
        log.info("根据分类id查询菜品及口味......");

        //构造redis中的key dish_分类id
        String key = "dish_" + categoryId;

        //查询redis中是否存在菜品数据
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);


        if(list != null && !list.isEmpty()) {
            //如果存在，直接返回
            return Result.success(list);
        }

        //如果不存在，查询数据库，查询到的数据放入redis
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE); //查询起售中的菜品

        list = dishService.getDishesWithFlavorsByCategoryId(dish);
        redisTemplate.opsForValue().set(key, list);
        return Result.success(list);
    }
}

package com.sky.product.controller.inner;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.product.service.DishService;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController("innerDishController")
@RequestMapping("/dish")
@Slf4j
@Api(tags = "内部菜品接口")
public class DishController {

    @Autowired
    private DishService dishService;

    @GetMapping("/{id}")
    public Dish getDishById(@PathVariable Long id) {
        return dishService.getDishById(id);
    }

    @GetMapping("/category/{categoryId}")
    public Integer countDishByCategoryId(@PathVariable Long categoryId) {
        return dishService.countByCategoryId(categoryId);
    }

    @GetMapping("/total")
    public Integer countDishByMap(@RequestParam Map map) {
        return dishService.countByMap(map);
    }


}

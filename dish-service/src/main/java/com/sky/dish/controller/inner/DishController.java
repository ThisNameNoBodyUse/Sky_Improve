package com.sky.dish.controller.inner;

import com.sky.dish.service.DishService;
import com.sky.entity.Dish;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController("InnerDishController")
@RequestMapping("/dish")
@Slf4j
@Api(tags = "内部菜品接口")
public class DishController {

    @Autowired
    private DishService dishService;

    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Dish getDishById(@PathVariable Long id) {
        return dishService.getById(id);
    }

    @GetMapping("/category/{categoryId}")
    @ApiOperation("根据分类id查询菜品数量")
    public Integer countByCategoryId(@PathVariable Long categoryId) {
        return dishService.countByCategoryId(categoryId);
    }


}

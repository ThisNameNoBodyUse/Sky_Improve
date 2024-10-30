package com.sky.setmeal.controller.inner;

import com.sky.entity.Setmeal;
import com.sky.setmeal.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController("InnerSetmealController")
@RequestMapping("/setmeal")
@Slf4j
@Api(tags = "内部套餐接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Setmeal getSetmealById(@PathVariable Long id) {
        return setmealService.getSetmealById(id);
    }

    @GetMapping("/category/{categoryId}")
    @ApiOperation("根据分类id查询套餐数量")
    public Integer countByCategoryId(@PathVariable Long categoryId) {
        return setmealService.countByCategoryId(categoryId);
    }

    @GetMapping("/dishIds")
    @ApiOperation("根据菜品id查询对应的套餐id")
    public List<Long> getSetmealIdsByDishIds(@Parameter List<Long> dishIds) {
        return setmealService.getSetmealIdsByDishIds(dishIds);
    }





}

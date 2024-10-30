package com.sky.product.controller.inner;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.product.service.SetmealService;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController("innerSetmealController")
@RequestMapping("/setmeal")
@Slf4j
@Api(tags = "内部套餐相关")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @GetMapping("/{id}")
    public Setmeal getSetmealById(@PathVariable Long id) {
        return setmealService.getSetmealById(id);
    }

    @GetMapping("/category/{categoryId}")
    public Integer countSetmealByCategoryId(@PathVariable Long categoryId) {
        return setmealService.countSetmealByCategoryId(categoryId);
    }

    @GetMapping("/dishIds")
    public List<Long> getSetmealIdsByDishIds(@Parameter List<Long> dishIds) {
        return setmealService.getSetmealIdsByDishIds(dishIds);
    }

    @GetMapping("/total")
    public Integer countSetmealByMap(@RequestParam Map map) {
        return setmealService.countByMap(map);
    }


}

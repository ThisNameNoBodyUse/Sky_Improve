package com.sky.api.client;

import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient("product-service")
public interface ProductClient {
    /**
     * 菜品部分
     *
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    Dish getDishById(@PathVariable Long id);


    @GetMapping("/dish/category/{categoryId}")
    Integer countDishByCategoryId(@PathVariable Long categoryId);


    @GetMapping("/dish/total")
    Integer countDishByMap(@RequestParam Map map);


    /**
     * 套餐部分
     */
    @GetMapping("/setmeal/{id}")
    Setmeal getSetmealById(@PathVariable Long id);

    @GetMapping("/setmeal/category/{categoryId}")
    Integer countSetmealByCategoryId(@PathVariable Long categoryId);

    @GetMapping("/setmeal/dishIds")
    List<Long> getSetmealIdsByDishIds(@Parameter List<Long> dishIds);

    @GetMapping("/setmeal/total")
    Integer countSetmealByMap(@RequestParam Map map);




}

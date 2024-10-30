package com.sky.product.controller.admin;


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
import java.util.Set;

@RestController("adminDishController")
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品 : {}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page") //没有RequestBody
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询 : {}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageResult(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    //TODO 菜品批量删除
    /**
     * 菜品批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("菜品批量删除 : {}", ids);
        dishService.deleteBatch(ids);

        //清空所有菜品缓存数据
        cleanCache("dish_*");

        return Result.success();
    }


    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据菜品id查询菜品 : {}", id);
        DishVO dishVO = dishService.getByIdWithFlavors(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     *
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品");
        dishService.updateWithFlavors(dishDTO);

        //清空所有菜品缓存数据
        cleanCache("dish_*");

        return Result.success();

    }

    @PostMapping("/status/{status}")
    @ApiOperation("起售/停售菜品")
    public Result updateStatus(@PathVariable Integer status, Long id) {
        log.info("起售/停售菜品 : {},{}", status, id);
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishService.updateStatus(dish);

        //清空所有菜品缓存数据
        cleanCache("dish_*");

        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> getDishesByCategoryId(Long categoryId) {
        log.info("根据分类id查询菜品 : {}", categoryId);
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        List<Dish> dishes = dishService.getByCategoryId(dish);
        return Result.success(dishes);
    }


    private void cleanCache(String patten) {

        //清空所有菜品缓存数据
        Set keys = redisTemplate.keys(patten);
        redisTemplate.delete(keys);
    }


}

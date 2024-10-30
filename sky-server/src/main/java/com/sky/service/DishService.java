package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    void saveWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageResult(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 菜品批量删除
     * @param ids
     */
    void deleteBatch(List<Long> ids);


    /**
     * 根据菜品id查询菜品和口味数据
     * @param id
     * @return
     */
    DishVO getByIdWithFlavors(Long id);

    /**
     * 修改菜品和口味
     * @param dishDTO
     */
    void updateWithFlavors(DishDTO dishDTO);

    /**
     * 起售/停售菜品
     * @param dish
     */
    void updateStatus(Dish dish);

    /**
     * 根据分类id查询菜品
     * @param dish
     * @return
     */
    List<Dish> getByCategoryId(Dish dish);

    /**
     * 根据分类id查询菜品及口味
     * @param dish
     * @return
     */
    List<DishVO> getDishesWithFlavorsByCategoryId(Dish dish);
}

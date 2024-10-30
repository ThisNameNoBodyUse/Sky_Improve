package com.sky.setmeal.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 新增套餐和菜品数据
     * @param setmealDTO
     */
    void saveWithDishes(SetmealDTO setmealDTO);

    /**
     * 批量删除套餐
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    SetmealVO getByIdWithDishes(Long id);

    /**
     * 修改套餐
     * @param setmealDTO
     */
    void updateWithDishes(SetmealDTO setmealDTO);

    /**
     * 套餐起售停售
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据分类id查询套餐
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据套餐id获取菜品项
     * @param id
     * @return
     */
    List<DishItemVO> getDishesItemById(Long id);

    /**
     * 根据套餐id查询套餐
     * @param id
     * @return
     */
    Setmeal getSetmealById(Long id);

    /**
     * 根据分类id查询套餐数量
     * @param categoryId
     * @return
     */
    Integer countByCategoryId(Long categoryId);

    /**
     * 根据菜品id查询对应的套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);
}

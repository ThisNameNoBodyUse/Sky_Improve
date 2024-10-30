package com.sky.product.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;

import com.sky.product.mapper.DishMapper;
import com.sky.product.mapper.SetmealDishMapper;
import com.sky.product.mapper.SetmealMapper;
import com.sky.product.service.SetmealService;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());

        Page<SetmealVO> page = setmealMapper.page(setmealPageQueryDTO);

        return new PageResult(page.getTotal(),page.getResult());

    }

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    public void saveWithDishes(SetmealDTO setmealDTO) {
        //插入1条数据套餐表
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.insert(setmeal);

        //插入n条数据套餐_菜品表
        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes != null && !setmealDishes.isEmpty()){
            for(SetmealDish setmealDish : setmealDishes){
                setmealDish.setSetmealId(setmealId);
            }
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Transactional
    public void batchDelete(List<Long> ids) {
        //先判断是否有套餐在起售中
        for(Long id : ids){
            //根据套餐id获取套餐
            Setmeal setmeal = setmealMapper.getById(id);
            if(setmeal.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        //批量删除套餐
        setmealMapper.deleteByIds(ids);

        //批量删除套餐和菜品关联表
        setmealDishMapper.deleteBySetmealIds(ids);

    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO getByIdWithDishes(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        //获取套餐菜品关联
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Transactional
    public void updateWithDishes(SetmealDTO setmealDTO) {
        //修改setmeal表
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);

        //删除setmeal_dish表对应的表项
        Long setmealId = setmealDTO.getId();
        setmealDishMapper.deleteBySetmealId(setmealId);

        //插入setmeal_dish表
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes != null && !setmealDishes.isEmpty()){
            for(SetmealDish setmealDish : setmealDishes){
                setmealDish.setSetmealId(setmealId);
            }
            setmealDishMapper.insertBatch(setmealDishes);
        }


    }

    /**
     * 套餐起售停售
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        setmeal.setId(id);
        setmealMapper.update(setmeal);
    }

    /**
     * 根据分类id查询套餐
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> setmeals = setmealMapper.list(setmeal);
        return setmeals;
    }

    /**
     * 根据套餐id获取菜品项
     * @param id
     * @return
     */
    @Transactional
    public List<DishItemVO> getDishesItemById(Long id) {
        return setmealMapper.getDishesItemById(id);
    }

    /**
     * 根据套餐id查询菜品
     * @param id
     * @return
     */
    @Override
    public Setmeal getSetmealById(Long id) {
        return setmealMapper.getById(id);
    }

    /**
     * 根据分类id查询套餐数量
     * @param categoryId
     * @return
     */
    @Override
    public Integer countSetmealByCategoryId(Long categoryId) {
        return setmealMapper.countByCategoryId(categoryId);
    }

    /**
     * 根据菜品id查询对应的套餐id
     * @param dishIds
     * @return
     */
    @Override
    public List<Long> getSetmealIdsByDishIds(List<Long> dishIds) {
        return setmealDishMapper.getSetmealIdsByDishIds(dishIds);
    }

    /**
     * 根据条件统计套餐数量
     * @param map
     * @return
     */
    @Override
    public Integer countByMap(Map map) {
        return setmealMapper.countByMap(map);
    }


}

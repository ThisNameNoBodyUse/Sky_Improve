package com.sky.product.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.product.mapper.DishFlavorMapper;
import com.sky.product.mapper.DishMapper;
import com.sky.product.mapper.SetmealDishMapper;
import com.sky.product.service.DishService;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
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
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品和对应的口味
     *
     * @param dishDTO
     */
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //向菜品表插入1条数据
        dishMapper.insert(dish);

        //获取insert生成的主键值
        Long dishId = dish.getId();
        log.info("dishId : {}", dishId);

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            //向口味表插入n条数据
            dishFlavorMapper.insertBach(flavors);
        }
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageResult(DishPageQueryDTO dishPageQueryDTO) {
        //开始分页
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());

    }

    /**
     * 菜品批量删除
     *
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断当前是否存在起售中的菜品
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                //当前菜品处于起售中，不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断是否存在菜品被套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && !setmealIds.isEmpty()) {
            //当前菜品被套餐关联，不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品表中的菜品数据
//        for(Long id : ids) {
//            dishMapper.deleteById(id);
//            //删除菜品关联的口味数据
//            dishFlavorMapper.deleteByDishId(id);
//        }

        //优化，解决性能问题
        //根据菜品id集合批量删除菜品数据
        dishMapper.deleteByIds(ids);

        //根据菜品id集合批量删除口味数据
        dishFlavorMapper.deleteByDishIds(ids);

    }


    /**
     * 根据菜品id查询菜品和口味数据
     *
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavors(Long id) {

        //根据id查询菜品数据
        Dish dish = dishMapper.getById(id);

        //根据菜品id查询菜品口味数据
        List<DishFlavor> flavors = dishFlavorMapper.getDishFlavors(id);

        //将查询到的数据封装到VO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;

    }

    /**
     * 修改菜品信息和口味
     *
     * @param dishDTO
     */
    @Override
    public void updateWithFlavors(DishDTO dishDTO) {

        //修改菜品表数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);


        //删除dishFlavor表中对应的口味数据
        Long dishId = dishDTO.getId();
        dishFlavorMapper.deleteByDishId(dishId);


        //重新插入口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            //批量插入口味数据
            dishFlavorMapper.insertBach(flavors);
        }


    }

    /**
     * 起售/停售菜品
     * @param dish
     */
    @Override
    public void updateStatus(Dish dish) {
        dishMapper.update(dish);
    }

    /**
     * 根据分类id查询菜品
     * @param dish
     * @return
     */
    @Override
    public List<Dish> getByCategoryId(Dish dish) {
        return dishMapper.list(dish);
    }

    /**
     * 根据分类id查询起售中的菜品及其口味
     * @param dish
     * @return
     */
    @Override
    public List<DishVO> getDishesWithFlavorsByCategoryId(Dish dish) {
        //根据分类id查询起售中的菜品和口味
        List<Dish> dishes = dishMapper.list(dish);
        return dishes.stream().map((item) -> {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(item, dishVO);
            Long dishId = item.getId();
            //根据菜品id查找口味
            List<DishFlavor> dishFlavors = dishFlavorMapper.getDishFlavors(dishId);
            dishVO.setFlavors(dishFlavors);
            return dishVO;
        }).collect(Collectors.toList());
    }

    @Override
    public Dish getDishById(Long id) {
        return dishMapper.getById(id);
    }

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Override
    public Integer countByCategoryId(Long categoryId) {
        return dishMapper.countByCategoryId(categoryId);
    }

    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    @Override
    public Integer countByMap(Map map) {
        return dishMapper.countByMap(map);
    }


}

package com.sky.cart.service.impl;

import com.sky.api.client.ProductClient;
import com.sky.cart.mapper.ShoppingCartMapper;
import com.sky.cart.service.ShoppingCartService;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    private final ProductClient productClient;


    /**
     * 添加购物车
     *
     * @param dto
     */
    //TODO 添加购物车
    @Override
    @GlobalTransactional
    public void addShoppingCart(ShoppingCartDTO dto) {

        //判断当前加入购物车的商品是否已经存在
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(dto, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentUserId());

        log.info("userId = {}", BaseContext.getCurrentUserId());


        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        //如果已经存在，数量+1
        if (list != null && !list.isEmpty()) {
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(cart);
        } else {
            //不存在，插入一条购物车数据

            //判断本次添加到购物车的是菜品还是套餐
            Long dishId = dto.getDishId();
            if (dishId != null) {
                //本次添加到购物车的是菜品
                Dish dish = productClient.getDishById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setImage(dish.getImage());
            } else {
                //本次添加到购物车的是套餐
                Setmeal setmeal = productClient.getSetmealById(dto.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setImage(setmeal.getImage());
            }
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setNumber(1);

            shoppingCartMapper.insert(shoppingCart);
        }


    }

    /**
     * 减少购物车
     *
     * @param dto
     */
    @Override
    public void subShoppingCart(ShoppingCartDTO dto) {
        //查看减少的商品的原本数量
        ShoppingCart cart = new ShoppingCart();
        BeanUtils.copyProperties(dto, cart);
        cart.setUserId(BaseContext.getCurrentUserId());

        List<ShoppingCart> list = shoppingCartMapper.list(cart);
        if (list != null && !list.isEmpty()) {
            ShoppingCart shoppingCart = list.get(0);
            Integer number = shoppingCart.getNumber();
            if (number == 1) {
                //删除该商品
                shoppingCartMapper.deleteById(shoppingCart);

            } else {
                //该商品数量-1
                shoppingCart.setNumber(number - 1);
                shoppingCartMapper.updateNumberById(shoppingCart);
            }
        }

    }

    @Override
    public List<ShoppingCart> showShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentUserId());
        return shoppingCartMapper.list(shoppingCart);
    }

    @Override
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentUserId();
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(userId).build();
        shoppingCartMapper.deleteById(shoppingCart);
    }

    /**
     * 根据购物车中的用户id查询购物车
     * @param cart
     * @return
     */
    @Override
    public List<ShoppingCart> getCartById(ShoppingCart cart) {
        return shoppingCartMapper.list(cart);
    }

    /**
     * 根据购物车中的用户id清空购物车
     * @param cart
     */
    @Override
    public void deleteCartById(ShoppingCart cart) {
        shoppingCartMapper.deleteById(cart);
    }

    @Override
    public void insertBatch(List<ShoppingCart> carts) {
        shoppingCartMapper.insertBatch(carts);
    }
}

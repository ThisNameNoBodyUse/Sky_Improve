package com.sky.cart.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    /**
     * 添加购物车
     * @param dto
     */
    void addShoppingCart(ShoppingCartDTO dto);

    /**
     * 减少购物车
     * @param dto
     */
    void subShoppingCart(ShoppingCartDTO dto);

    /**
     * 查看购物车
     * @return
     */
    List<ShoppingCart> showShoppingCart();

    /**
     * 清空购物车
     */
    void cleanShoppingCart();

    /**
     * 根据购物车中的用户id查询购物车
     * @param cart
     * @return
     */
    List<ShoppingCart> getCartById(ShoppingCart cart);

    /**
     * 根据购物车中的用户id清空购物车
     * @param cart
     */
    void deleteCartById(ShoppingCart cart);

    /**
     * 批量插入购物车
     * @param carts
     */
    void insertBatch(List<ShoppingCart> carts);
}

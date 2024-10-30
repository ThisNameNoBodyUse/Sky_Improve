package com.sky.api.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sky.entity.ShoppingCart;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("cart-service")
public interface ShoppingCartClient {

    @GetMapping("/shoppingCart/list")
    List<ShoppingCart> getCartById(@RequestParam("cart") String cartJson) throws JsonProcessingException;

    @DeleteMapping("/shoppingCart/clean")
    void deleteCartById(@RequestBody ShoppingCart cart);

    @PostMapping("/shoppingCart")
    void CartInsertBatch(@RequestBody List<ShoppingCart> carts);

}

package com.sky.cart.controller.inner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.cart.service.ShoppingCartService;
import com.sky.entity.ShoppingCart;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("innerShoppingCart")
@RequestMapping("/shoppingCart")
@Api(tags = "内部购物车接口")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public List<ShoppingCart> getCartById(@RequestParam("cart") String cartJson) throws JsonProcessingException {
        ShoppingCart cart = new ObjectMapper().readValue(cartJson,ShoppingCart.class);
        return shoppingCartService.getCartById(cart);
    }

    @DeleteMapping("/clean")
    public void deleteCartById(@RequestBody ShoppingCart cart) {
        shoppingCartService.deleteCartById(cart);
    }

    @PostMapping
    public void CartInsertBatch(@RequestBody List<ShoppingCart> carts) {
        shoppingCartService.insertBatch(carts);
    }


}

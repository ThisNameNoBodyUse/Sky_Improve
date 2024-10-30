package com.sky.cart.controller.user;

import com.sky.cart.service.ShoppingCartService;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userShoppingCart")
@RequestMapping("/user/shoppingCart")
@Api(tags = "C端购物车相关接口")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     *
     * @param dto
     * @return
     */
    //TODO 添加购物车
    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result addShoppingCart(@RequestBody ShoppingCartDTO dto) {
        log.info("添加购物车 : {}", dto);

        shoppingCartService.addShoppingCart(dto);

        return Result.success();
    }

    /**
     * 减少购物车
     *
     * @param dto
     * @return
     */
    @PostMapping("/sub")
    @ApiOperation("减少购物车")
    public Result subShoppingCart(@RequestBody ShoppingCartDTO dto) {
        log.info("减少购物车 : {}", dto);

        shoppingCartService.subShoppingCart(dto);

        return Result.success();
    }


    /**
     * 查看购物车
     *
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查看购物车")
    public Result<List<ShoppingCart>> list() {
        log.info("查看购物车......");
        List<ShoppingCart> list = shoppingCartService.showShoppingCart();
        return Result.success(list);
    }


    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
    public Result clean() {
        log.info("清空购物车......");
        shoppingCartService.cleanShoppingCart();
        return Result.success();
    }
}

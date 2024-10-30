package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrdersService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrdersController")
@RequestMapping("/admin/order")
@Api(tags = "订单管理接口")
@Slf4j
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    /**
     * 订单搜索
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("订单搜索 : {}", ordersPageQueryDTO);
        PageResult pageResult = ordersService.query(ordersPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 各个状态的订单数量统计
     *
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("各个状态的订单数量统计")
    public Result<OrderStatisticsVO> getOrderStatistics() {
        log.info("各个状态的订单数量统计......");
        OrderStatisticsVO orderStatisticsVO = ordersService.getOrderStatistics();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> getDetailsById(@PathVariable Long id) {
        Orders orders = new Orders();
        orders.setId(id);
        OrderVO orderVO = ordersService.getById(orders);
        return Result.success(orderVO);
    }

    /**
     * 接单
     *
     * @param ordersConfirmDTO
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result Confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("接单 : {}", ordersConfirmDTO);
        ordersService.confirm(ordersConfirmDTO);
        return Result.success();
    }


    /**
     * 拒单
     *
     * @param ordersRejectionDTO
     * @return
     */
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result Reject(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("拒单 : {}", ordersRejectionDTO);
        ordersService.reject(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 取消订单
     *
     * @param ordersCancelDTO
     * @return
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result Cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("取消订单 : {}", ordersCancelDTO);
        ordersService.cancel(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 派送
     *
     * @param id
     * @return
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送")
    public Result delivery(@PathVariable Long id) {
        log.info("派送订单 : {}", id);
        ordersService.delivery(id);
        return Result.success();
    }

    /**
     * 完成订单
     *
     * @param id
     * @return
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result Complete(@PathVariable Long id) {
        log.info("完成订单 : {}", id);
        ordersService.comlete(id);
        return Result.success();
    }

}

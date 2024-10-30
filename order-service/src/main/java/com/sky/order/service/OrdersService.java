package com.sky.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrdersService {
    /**
     * 用户下单
     *
     * @param ordersSubmitDTO
     * @return
     */
    //TODO 用户下单
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) throws JsonProcessingException;


    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    //TODO 订单支付
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 历史订单查询
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult page(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单详情
     * @param orders
     * @return
     */
    OrderVO getById(Orders orders);

    /**
     * 通过订单id取消订单
     * @param id
     */
    void cancelOrderById(Long id);

    /**
     * 再来一单
     * @param id
     */
    //TODO 再来一单
    void repeatOrder(Long id);

    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult query(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 各个状态的订单数量统计
     * @return
     */
    OrderStatisticsVO getOrderStatistics();

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    void reject(OrdersRejectionDTO ordersRejectionDTO);


    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    void cancel(OrdersCancelDTO ordersCancelDTO);

    /**
     * 派送订单
     * @param id
     */
    void delivery(Long id);

    /**
     * 完成订单
     * @param id
     */
    void comlete(Long id);


    /**
     * 客户催单
     * @param id
     */
    void reminder(Long id);

    /**
     * 查询指定订单数
     * @param map
     * @return
     */
    Integer countByMap(Map map);

    /**
     * 根据动态条件统计营业额数据
     * @param map
     * @return
     */
    Double sumByMap(Map map);

    /**
     * 统计指定时间区间内的销量排名前十
     * @param beginTime
     * @param endTime
     * @return
     */
    List<GoodsSalesDTO> getSalesTop10(LocalDateTime beginTime, LocalDateTime endTime);
}

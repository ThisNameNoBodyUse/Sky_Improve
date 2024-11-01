package com.sky.order.task;

import com.sky.entity.Orders;
import com.sky.order.mapper.OrdersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类，定时处理订单状态
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrdersMapper ordersMapper;

    /**
     * 处理超时订单的方法
     */
    @Scheduled(cron = "0 * * * * ?") //每分钟触发一次
    //@Scheduled(cron = "1/5 * * * * ?") //从第1s开始每隔5s触发一次
    public void processTimeoutOrder() {
        log.info("定时处理超时订单 : {}", LocalDateTime.now());

        // 当前时间 ，往前推15分钟的时间之前生成的订单如果还未支付，就超时
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        //select * from orders where status = ? and order_time < (当前时间 - 15)
        List<Orders> list = ordersMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);
        if (list != null && !list.isEmpty()) {
            //遍历集合，修改订单状态
            for (Orders orders : list) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
                ordersMapper.update(orders);
            }
        }

    }

    /**
     * 处理一直处于派送中状态的订单
     */
    @Scheduled(cron = "0 0 1 * * ?") //固定每天凌晨一点触发一次
    //@Scheduled(cron = "0/5 * * * * ?") //每隔5s触发一次
    public void processDeliveryOrder() {
        log.info("定时处理处于派送中的订单 : {}", LocalDateTime.now());

        //当前时间是凌晨1点，-60就是凌晨0点，也就是上一个工作日的最终时间
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        List<Orders> list = ordersMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);
        if (list != null && !list.isEmpty()) {
            for (Orders orders : list) {
                orders.setStatus(Orders.COMPLETED); //自动完成
                ordersMapper.update(orders);
            }
        }

    }
}

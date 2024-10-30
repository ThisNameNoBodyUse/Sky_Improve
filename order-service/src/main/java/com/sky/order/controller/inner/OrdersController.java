package com.sky.order.controller.inner;


import com.sky.dto.GoodsSalesDTO;
import com.sky.order.service.OrdersService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController("InnerOrdersController")
@RequestMapping("/order")
@Api(tags = "内部订单接口")
@Slf4j
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @GetMapping("/total")
    public Integer countByMap(@RequestParam Map map) {
        return ordersService.countByMap(map);
    }

    @GetMapping("/sum")
    public Double sumByMap(@RequestParam Map map) {
        return ordersService.sumByMap(map);
    }

    @GetMapping("/salesTop10")
    public List<GoodsSalesDTO> getSalesTop10(@RequestParam LocalDateTime beginTime, @RequestParam LocalDateTime endTime) {
        return ordersService.getSalesTop10(beginTime, endTime);
    }

}

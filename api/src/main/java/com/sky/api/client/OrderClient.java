package com.sky.api.client;

import com.sky.dto.GoodsSalesDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@FeignClient("order-service")
public interface OrderClient {

    @GetMapping("/order/total")
    Integer countByMap(@RequestParam Map map);


    @GetMapping("/order/sum")
    Double sumByMap(@RequestParam Map map);

    @GetMapping("/order/salesTop10")
    List<GoodsSalesDTO> getSalesTop10(@RequestParam LocalDateTime beginTime, @RequestParam LocalDateTime endTime);


}

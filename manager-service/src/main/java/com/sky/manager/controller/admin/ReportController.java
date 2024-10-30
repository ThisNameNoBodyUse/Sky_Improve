package com.sky.manager.controller.admin;

import com.sky.manager.service.ReportService;
import com.sky.result.Result;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Api(tags = "数据统计相关接口")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 用户统计
     *
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> turnOverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("营业额数据统计：{},{}", begin, end);
        TurnoverReportVO vo = reportService.getTurnOverStatistics(begin, end);
        return Result.success(vo);

    }

    /**
     * 用户统计
     *
     * @return
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("用户统计 ： {},{}", begin, end);
        UserReportVO userReportVO = reportService.getUserStatistics(begin, end);
        return Result.success(userReportVO);

    }

    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计")
    public Result<OrderReportVO> getOrdersStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("订单统计 : {},{}", begin, end);
        OrderReportVO vo = reportService.getOrdersStatistics(begin, end);
        return Result.success(vo);

    }

    /**
     * 查询销量统计top10
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/top10")
    @ApiOperation("查询销量统计top10")
    public Result<SalesTop10ReportVO> getTop10SalesStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("查询销量统计top10 : {},{}", begin, end);
        SalesTop10ReportVO vo = reportService.getTop10SalesStatistics(begin,end);
        return Result.success(vo);

    }

    /**
     * 导出运营数据报表
     * @return
     */
    @GetMapping("/export")
    @ApiOperation("导出运营数据报表")
    public void export(HttpServletResponse response) throws IOException {

        //通过response获取输出流，通过输出流就可以将excel文件下载到客户端浏览器
        reportService.exportBusinessData(response);
    }

}

package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 统计指定时间区间内的营业额数据
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnOverStatistics(LocalDate begin, LocalDate end) {

        //当前集合用于存储从begin到end范围内每天的日期
        List<LocalDate> dateList = getDateList(begin, end);

        //存放每天的营业额
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            //查询date日期对应的营业额数据（状态为已完成的订单金额合计）
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN); //00:00 开始时刻
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX); //23:59 结束时刻

            //select sum(amount) from orders where order_time > beginTime and order_time < endTime and status = 5
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = ordersMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover; //等于空，返回0.0
            turnoverList.add(turnover);
        }


        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 统计指定时间区间内的用户数据
     *
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {

        //当前集合用于存储从begin到end范围内每天的日期
        List<LocalDate> dateList = getDateList(begin, end);
        //每一天新增的用户数量 select count(id) from user where create_time < ? and create_time > ?
        List<Integer> newUserList = new ArrayList<>();
        //截止到每一天的用户总数 select count(id) from user where create_time < ?
        List<Integer> totalUserList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN); //开始时刻
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX); //结束时刻

            Map map = new HashMap();
            map.put("end", endTime);
            Integer totalUser = userMapper.countByMap(map); //截止到一天结束的用户总数
            map.put("begin", beginTime);
            Integer newUser = userMapper.countByMap(map); //在这一天之内新增的用户数量

            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }

        //封装结果数据
        return UserReportVO
                .builder()
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .dateList(StringUtils.join(dateList, ","))
                .build();
    }

    /**
     * 统计指定时间内的订单数据和总数据
     *
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();

        //时间区间内的订单总数
        Integer totalOrderCount = 0;

        //时间区间内的有效订单数
        Integer totalValidOrderCount = 0;

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN); //开始时刻
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX); //结束时刻

            //查询每天的订单总数 select count(id) from orders where order_time > beginTime and order_time < endTime
            Integer orderCount = getOrderCount(beginTime, endTime, null);
            orderCountList.add(orderCount);
            totalOrderCount += orderCount;

            //查询每天的有效订单数 select count(id) from orders where order_time > beginTime and order_time < endTime and status = 5
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);
            validOrderCountList.add(validOrderCount);
            totalValidOrderCount += validOrderCount;
        }


        Double orderCompleteRate = 0.0;
        if (totalOrderCount != 0) {
            //计算订单完成率
            orderCompleteRate = totalValidOrderCount.doubleValue() / totalOrderCount;
        }

        return OrderReportVO
                .builder()
                .totalOrderCount(totalOrderCount)
                .dateList(StringUtils.join(dateList, ",")) //拼接列表为字符串，用","分割
                .validOrderCount(totalValidOrderCount)
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .orderCompletionRate(orderCompleteRate)
                .build();


    }

    /**
     * 查询指定时间内销量排名前10
     *
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO getTop10SalesStatistics(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> salesTop10 = ordersMapper.getSalesTop10(beginTime, endTime);

        //使用流来获取列表
        List<String> nameList = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numberList = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        return SalesTop10ReportVO
                .builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();

    }

    /**
     * 导出运营数据报表
     *
     * @param response
     */
    public void exportBusinessData(HttpServletResponse response) throws IOException {
        //1. 查询数据库 获取营业数据 ---查询最近30天的运营数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1); //截止到昨天的最近30天数据

        BusinessDataVO vo = workspaceService.getBusinessData(LocalDateTime.of(dateBegin, LocalTime.MIN), LocalDateTime.of(dateEnd, LocalTime.MAX));


        //2. 通过POI将数据写入excel文件
        //获取当前类对象的类加载器的资源（从这个类路径下获取资源,指向src/main/resources这个文件夹）
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        //基于模板文件创建一个新的excel文件
        XSSFWorkbook excel = new XSSFWorkbook(in);

        //获取表格文件的Sheet页
        XSSFSheet sheet = excel.getSheet("Sheet1");

        //获取第二行第二个单元格并填充时间
        sheet.getRow(1).getCell(1).setCellValue("时间：" + dateBegin + "至" + dateEnd);

        //获得第四行
        XSSFRow row = sheet.getRow(3);
        row.getCell(2).setCellValue(vo.getTurnover()); //营业额
        row.getCell(4).setCellValue(vo.getOrderCompletionRate()); //订单完成率
        row.getCell(6).setCellValue(vo.getNewUsers()); //新增用户数

        //获得第五行
        row = sheet.getRow(4);
        row.getCell(2).setCellValue(vo.getValidOrderCount()); //有效订单数
        row.getCell(4).setCellValue(vo.getUnitPrice());  //平均客单价

        //填充明细数据，有30天，循环30次
        for (int i = 0; i < 30; i++) {
            LocalDate date = dateBegin.plusDays(i); //从起始时间开始计算，每一次推后一天，直到第30天
            BusinessDataVO bd = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));

            //获得某一行
            row = sheet.getRow(7 + i);

            row.getCell(1).setCellValue(date.toString()); //日期
            row.getCell(2).setCellValue(bd.getTurnover()); //营业额
            row.getCell(3).setCellValue(bd.getValidOrderCount()); //有效订单
            row.getCell(4).setCellValue(bd.getOrderCompletionRate()); //订单完成率
            row.getCell(5).setCellValue(bd.getUnitPrice()); //平均客单价
            row.getCell(6).setCellValue(bd.getNewUsers()); //新增用户数
        }


        //3.通过输出流将excel文件下载到客户端浏览器
        ServletOutputStream out = response.getOutputStream();
        excel.write(out);

        //关闭资源
        out.close();
        excel.close();

    }


    /**
     * 获取日期列表
     *
     * @param begin
     * @param end
     * @return
     */
    private List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        //当前集合用于存储从begin到end范围内每天的日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            //日期计算,计算指定日期的后一天对应的日期
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        dateList.add(end);
        return dateList;
    }

    /**
     * 根据条件统计订单数量
     *
     * @param begin
     * @param end
     * @param status
     * @return
     */
    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);

        return ordersMapper.countByMap(map);

    }

}

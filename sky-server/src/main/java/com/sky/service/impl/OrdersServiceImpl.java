package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrdersService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl implements OrdersService {
    @Autowired
    private OrdersMapper orderMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatPayUtil weChatPayUtil;

    @Autowired
    private WebSocketServer webSocketServer;


    /**
     * 用户下单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {

        //处理各种业务异常（地址簿为空，购物车数据为空）
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            //抛出业务异常
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        Long userId = BaseContext.getCurrentId();
        ShoppingCart cart = new ShoppingCart();

        //查找当前用户的购物车数据
        cart.setUserId(userId);
        List<ShoppingCart> list = shoppingCartMapper.list(cart);

        if (list == null || list.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //向订单表插入1条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);

        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(0);
        orders.setAddress(
                addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail()
        );
        orders.setStatus(1); //待付款
        orders.setNumber(String.valueOf(System.currentTimeMillis()) + BaseContext.getCurrentId());//订单号
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());

        orderMapper.insert(orders);

        //向订单明细表插入n条数据
        List<OrderDetail> orderDetails = list.stream().map((shoppingCart) -> {
            OrderDetail orderDetail = new OrderDetail(); //订单明细
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            orderDetail.setOrderId(orders.getId()); //设置当前订单明细关联的订单id
            return orderDetail;
        }).collect(Collectors.toList());

        orderDetailMapper.insertBatch(orderDetails);

        //清空当前用户的购物车数据
        shoppingCartMapper.deleteById(cart);

        //封装VO返回结果
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();
    }


    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

//        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "饕餮盛宴订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }
//
//        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
//        vo.setPackageStr(jsonObject.getString("package"));
//
//        return vo;

        paySuccess(ordersPaymentDTO.getOrderNumber());
        String orderNumber = ordersPaymentDTO.getOrderNumber();//订单号
        Long orderId = orderMapper.getOrderId(orderNumber); //根据订单号查主键
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "ORDERPAID");
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));
        //为代替微信支付成功后的数据库订单状态更新，多定义一个方法进行修改
        Integer OrderPaidStatus = Orders.PAID; //支付状态 ： 已支付
        Integer OrderStatus = Orders.TO_BE_CONFIRMED; //订单状态 ： 待接单
        LocalDateTime check_out_time = LocalDateTime.now(); //支付时间
        orderMapper.updateStatus(OrderStatus, OrderPaidStatus, check_out_time, orderId);
        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) { //订单号

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

        //通过websocket向客户端浏览器推送消息
        Map<String, Object> map = new HashMap<>();
        map.put("type", 1); //1 表示来单提醒 2 表示客户催单
        map.put("orderId", orders.getId()); //订单id
        map.put("content", "订单号：" + outTradeNo);

        //map转json
        String json = JSONObject.toJSONString(map);
        webSocketServer.sendToAllClient(json);
    }

    /**
     * 历史订单查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    @Transactional
    public PageResult page(OrdersPageQueryDTO ordersPageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());

        Page<Orders> ordersPage = orderMapper.page(ordersPageQueryDTO);

        Long total = ordersPage.getTotal();
        List<Orders> records = ordersPage.getResult();

        List<OrderVO> orderVOS = records.stream().map((item) -> {
            OrderVO orderVO = new OrderVO();
            Long orderId = item.getId();
            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orderId);
            BeanUtils.copyProperties(item, orderVO);
            orderVO.setOrderDetailList(orderDetailList);

            return orderVO;
        }).collect(Collectors.toList());

        return new PageResult(total, orderVOS);
    }

    /**
     * 根据id查询订单详情
     *
     * @param orders
     * @return
     */
    @Override
    @Transactional
    public OrderVO getById(Orders orders) {

        orders = orderMapper.getById(orders);
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orders.getId());
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetails);
        return orderVO;
    }

    /**
     * 通过订单id取消订单
     *
     * @param id
     */
    @Override
    public void cancelOrderById(Long id) {

        Orders order = new Orders();
        order.setId(id);
        order = orderMapper.getById(order);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (order.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        order.setStatus(Orders.CANCELLED); //取消订单状态
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason("用户取消");
        orderMapper.update(order);

    }

    /**
     * 再来一单
     *
     * @param id
     */
    @Override
    @Transactional
    public void repeatOrder(Long id) {
        //根据订单id获取订单详情，插入到当前用户的购物车
        Long userId = BaseContext.getCurrentId(); //当前用户
        Orders order = new Orders();
        order.setId(id);
        order = orderMapper.getById(order);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        List<ShoppingCart> shoppingCarts = orderDetailList.stream().map((item) -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(item, shoppingCart);
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());

        //批量插入到购物车
        shoppingCartMapper.insertBatch(shoppingCarts);
    }

    /**
     * 订单搜索
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    @Transactional
    public PageResult query(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        Page<Orders> page = orderMapper.page(ordersPageQueryDTO);
        Long total = page.getTotal();
        List<Orders> records = page.getResult();
        List<OrderVO> orderVOS = transformToVO(records);
        return new PageResult(total, orderVOS);

    }

    /**
     * 各个状态的订单数量统计
     *
     * @return
     */
    @Override
    public OrderStatisticsVO getOrderStatistics() {
        //分别统计出待接单数量 待派送数量 派送中数量
        Integer toBeConfirmed = orderMapper.countStatus(Orders.TO_BE_CONFIRMED); //status = 2 待接单
        Integer confirmed = orderMapper.countStatus(Orders.CONFIRMED); //status = 3 待派送
        Integer deliveryInProgress = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS); //status = 4 派送中

        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        return orderStatisticsVO;
    }

    /**
     * 接单
     *
     * @param ordersConfirmDTO
     */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED).build();
        orderMapper.update(orders);
    }

    /**
     * 拒单
     *
     * @param ordersRejectionDTO
     */
    @Override
    public void reject(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders = Orders.builder().id(ordersRejectionDTO.getId()).build();
        orders = orderMapper.getById(orders);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //只有待接单状态才能拒单
        if (!Objects.equals(orders.getStatus(), Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());

        orderMapper.update(orders);

    }

    /**
     * 取消订单
     *
     * @param ordersCancelDTO
     */
    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = Orders.builder().id(ordersCancelDTO.getId()).build();
        orders = orderMapper.getById(orders);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        orders.setRejectionReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orders.setStatus(Orders.CANCELLED);
        orderMapper.update(orders);
    }

    /**
     * 派送
     *
     * @param id
     */
    @Override
    public void delivery(Long id) {
        Orders orders = Orders.builder().id(id).build();
        orders = orderMapper.getById(orders);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //只有待派送的订单才能派送
        if (!Objects.equals(orders.getStatus(), Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS); //派送中
        orderMapper.update(orders);
    }

    /**
     * 完成订单
     *
     * @param id
     */
    @Override
    public void comlete(Long id) {
        Orders orders = Orders.builder().id(id).build();
        orders = orderMapper.getById(orders);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //只有派送中的订单才能完成
        if (!Objects.equals(orders.getStatus(), Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //订单状态为已完成
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());
        orderMapper.update(orders);

    }

    /**
     * 客户催单
     *
     * @param id
     */
    @Override
    public void reminder(Long id) {
        Orders orders = Orders.builder().id(id).build();
        //订单是否存在
        if(orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        orders = orderMapper.getById(orders);
        Map<String, Object> map = new HashMap<>();
        map.put("type", 2); //1 为来单提醒 2 为客户催单
        map.put("orderId", id); //订单id
        map.put("content", "订单号：" + orders.getNumber());
        String json = JSONObject.toJSONString(map);
        webSocketServer.sendToAllClient(json);
    }


    /**
     * Orders列表转换为OrderVO列表
     *
     * @param records
     * @return
     */
    private List<OrderVO> transformToVO(List<Orders> records) {
        List<OrderVO> orderVOS = records.stream().map((item) -> {
            OrderVO orderVO = new OrderVO();
            Long orderId = item.getId();
            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orderId);
            BeanUtils.copyProperties(item, orderVO);
            orderVO.setOrderDishes(getStr(orderDetailList));
            return orderVO;
        }).collect(Collectors.toList());
        return orderVOS;
    }

    private String getStr(List<OrderDetail> orderDetailList) {
        StringBuilder str = new StringBuilder();
        for (OrderDetail orderDetail : orderDetailList) {
            str.append(orderDetail.getName()).append("*").append(orderDetail.getNumber()).append(" ");
        }
        return str.toString();

    }
}

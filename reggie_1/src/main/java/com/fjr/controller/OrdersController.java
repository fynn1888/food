package com.fjr.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fjr.common.BaseContext;
import com.fjr.common.R;
import com.fjr.dto.OrderDto;
import com.fjr.entity.OrderDetail;
import com.fjr.entity.Orders;
import com.fjr.service.OrderDetailService;
import com.fjr.service.OrdersService;
import com.sun.org.apache.xpath.internal.operations.Or;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {

    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 新增订单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> sub(@RequestBody Orders orders){
        ordersService.saveOrdersAndOrderDetails(orders);
        return R.success("下单成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page<OrderDto>> orderList(int page, int pageSize){
        //先将order信息查出
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        Page<Orders> ordersPage = new Page<>(page,pageSize);
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        Page<Orders> orderList = ordersService.page(ordersPage, queryWrapper);
        //新建dto分页对象
        Page<OrderDto> orderDtoPage = new Page<>(page, pageSize);
        //将原来的数据复制给dto的分页除了records
        BeanUtils.copyProperties(ordersPage,orderDtoPage,"records");
        //取出订单查询结果records
        List<Orders> ordersPageRecords = ordersPage.getRecords();
        //新建一个list<orderDto>接收处理后的数据
        List<OrderDto> list=null;
        //将records做处理，使用stream流，在里面新建一个dto对象然后把数据set进去返回dto对象
        list=ordersPageRecords.stream().map((item)->{
            OrderDto orderDto = new OrderDto();
            //将数据复制给dto
            BeanUtils.copyProperties(item,orderDto);
            //获取订单id查询订单详情,再将订单详情查询出来然后set进去
            String orderId = item.getNumber();
            LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderDetail::getOrderId,orderId);
            List<OrderDetail> detailList = orderDetailService.list(wrapper);
            orderDto.setOrderDetails(detailList);
            return orderDto;
        }).collect(Collectors.toList());
        orderDtoPage.setRecords(list);
        return R.success(orderDtoPage);
    }

    /**
     * 再来一单
     * @param orders
     * @return
     */
    @PostMapping("again")
    public R<String> again(@RequestBody Orders orders){
        //先查出订单信息
        Orders orders1 = ordersService.getById(orders);
        //将订单id置空让mybatis使用雪花算法生成
        orders1.setId(null);
        //将订单状态更改
        orders1.setStatus(2);
        //保存订单信息
        ordersService.save(orders1);
        //查出订单详情
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,orders.getNumber());
        //对订单详情做处理，把每个订单详情的id置空
        List<OrderDetail> list = orderDetailService.list(queryWrapper);
        list.stream().map((item)->{
            item.setId(null);
            return item;
        }).collect(Collectors.toList());
        //保存订单详情
        orderDetailService.saveBatch(list);
        return R.success("再来一单成功");
    }
}

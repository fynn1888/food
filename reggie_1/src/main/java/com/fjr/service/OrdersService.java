package com.fjr.service;

import com.fjr.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 广理最靓的仔
* @description 针对表【orders(订单表)】的数据库操作Service
* @createDate 2023-05-05 15:55:01
*/
public interface OrdersService extends IService<Orders> {

    public void saveOrdersAndOrderDetails(Orders orders);
}

package com.fjr.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fjr.entity.OrderDetail;
import com.fjr.service.OrderDetailService;
import com.fjr.mapper.OrderDetailMapper;
import org.springframework.stereotype.Service;

/**
* @author 广理最靓的仔
* @description 针对表【order_detail(订单明细表)】的数据库操作Service实现
* @createDate 2023-05-05 15:56:18
*/
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>
    implements OrderDetailService{

}





package com.fjr.mapper;

import com.fjr.entity.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 广理最靓的仔
* @description 针对表【orders(订单表)】的数据库操作Mapper
* @createDate 2023-05-05 15:55:01
* @Entity com.fjr.entity.Orders
*/
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

}





package com.fjr.mapper;

import com.fjr.entity.OrderDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 广理最靓的仔
* @description 针对表【order_detail(订单明细表)】的数据库操作Mapper
* @createDate 2023-05-05 15:56:18
* @Entity com.fjr.entity.OrderDetail
*/
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {

}





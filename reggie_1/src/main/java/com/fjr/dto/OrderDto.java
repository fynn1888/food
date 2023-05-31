package com.fjr.dto;

import com.fjr.entity.OrderDetail;
import com.fjr.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto extends Orders {
    private List<OrderDetail> orderDetails;
}

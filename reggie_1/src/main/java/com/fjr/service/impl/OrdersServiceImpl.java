package com.fjr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fjr.common.BaseContext;
import com.fjr.common.CustomException;
import com.fjr.entity.*;
import com.fjr.service.*;
import com.fjr.mapper.OrdersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
* @author 广理最靓的仔
* @description 针对表【orders(订单表)】的数据库操作Service实现
* @createDate 2023-05-05 15:55:01
*/
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
    implements OrdersService{

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Transactional
    public void saveOrdersAndOrderDetails(Orders orders) {
        //获取当前用户id
        Long currentId = BaseContext.getCurrentId();
        //遍历购物车校验如果购物车没东西就抛异常
        LambdaQueryWrapper<ShoppingCart> shoppingCartQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartQueryWrapper.eq(ShoppingCart::getUserId,currentId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(shoppingCartQueryWrapper);
        if (shoppingCarts==null||shoppingCarts.size()==0){
            throw new CustomException("购物车为空");
        }
        //校验地址如果为空就抛异常
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook==null){
            throw new CustomException("地址为空");
        }
        //生成订单号
        Long id = IdWorker.getId();
        //总金额，原子操作对象，大致了解是说stream流里操作数据要用原子对象操作
        AtomicInteger amount = new AtomicInteger(0);
        //菜品总数
        AtomicInteger sunNum = new AtomicInteger(0);
        //将订单详情设置完整
        List<OrderDetail> orderDetail = shoppingCarts.stream().map((item) -> {
            OrderDetail detail = new OrderDetail();
            detail.setName(item.getName());
            detail.setImage(item.getImage());
            detail.setOrderId(id);
            detail.setDishId(item.getDishId());
            detail.setSetmealId(item.getSetmealId());
            detail.setDishFlavor(item.getDishFlavor());
            detail.setNumber(item.getNumber());
            detail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            sunNum.addAndGet(item.getNumber().intValue());
            return detail;
        }).collect(Collectors.toList());

        //查询用户信息将当前订单信息设置完整插入订单表
        User user = userService.getById(currentId);
        orders.setNumber(String.valueOf(id));
        orders.setStatus(4);
        orders.setUserId(currentId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setPayMethod(1);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setPhone(user.getPhone());
        orders.setAddress((addressBook.getProvinceName()==null?"":addressBook.getProvinceName())
        +(addressBook.getCityName()==null?"":addressBook.getCityName())
        +(addressBook.getDistrictName()==null?"":addressBook.getDistrictName())
        +(addressBook.getDetail()==null?"":addressBook.getDetail())
        );
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        int i = sunNum.intValue();
        orders.setSumNum(i);
        this.save(orders);
        //插入多条数据给订单详情表
        orderDetailService.saveBatch(orderDetail);
        //清空购物车
        shoppingCartService.remove(shoppingCartQueryWrapper);
    }
}





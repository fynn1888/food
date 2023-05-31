package com.fjr.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fjr.common.BaseContext;
import com.fjr.common.R;
import com.fjr.entity.ShoppingCart;
import com.fjr.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 加入菜品或套餐到购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> addShop(@RequestBody ShoppingCart shoppingCart){
        //获取当前用户将id设置
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        //判断是加入菜品还是加入套餐
        if (shoppingCart.getDishId()!=null){
            //这里注意逻辑顺序，并不需要将全部逻辑都写进if/else里我最开始自己写是写成下面这个吊样，
            //没经大脑就直接一股脑的将逻辑全写进去，导致会多发出一条查询的sql，实际上只需要把queryWrapper添加条件
            //给加进判断里就好了，这样就只用发一条sql就可以将是否存在的结果查出来，原理就是判断是菜品还是套餐
            //如果是菜品就只添加查询菜品的条件，套餐反之
//        if (shoppingCart.getDishId()!=null){
//            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
//            ShoppingCart dishOne = shoppingCartService.getOne(queryWrapper);
//            if (dishOne!=null){
//                shoppingCart.setNumber(shoppingCart.getNumber()+1);
//                shoppingCartService.updateById(shoppingCart);
//            }else {
//                shoppingCart.setNumber(1);
//                shoppingCartService.save(shoppingCart);
//            }
//        }else {
//            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
//            ShoppingCart setMealOne = shoppingCartService.getOne(queryWrapper);
//            if (setMealOne!=null){
//                shoppingCart.setNumber(shoppingCart.getNumber()+1);
//                shoppingCartService.updateById(shoppingCart);
//            }else {
//                shoppingCart.setNumber(1);
//                shoppingCartService.save(shoppingCart);
//            }
//        }
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        //查询是否存在，不存在就默认设置数量为1，存在就将数量增加一update掉
        if (one!=null){
            one.setNumber(one.getNumber()+1);
            shoppingCartService.updateById(one);
        }else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one=shoppingCart;
        }
        return R.success(one);
    }

    /**
     * 购物车列表
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 减少数量
     * @param map
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody Map map){
        log.info(map.toString());
        Object dishId = map.get("dishId");
        Object setMealId = map.get("setmealId");
        //根据id查菜品还是套餐
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        if (dishId!=null){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,setMealId);
        }
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        //判断数量是否大于1，大于1就数量减一等于1就先将数量置0删除数据库数据，再返回对象
        if (one.getNumber()>1){
            one.setNumber(one.getNumber()-1);
            shoppingCartService.updateById(one);
        }else {
            one.setNumber(0);
            shoppingCartService.removeById(one);
        }
        return R.success(one);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车");
    }
}

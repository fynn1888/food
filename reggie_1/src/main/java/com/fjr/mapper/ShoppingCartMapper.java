package com.fjr.mapper;

import com.fjr.entity.ShoppingCart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 广理最靓的仔
* @description 针对表【shopping_cart(购物车)】的数据库操作Mapper
* @createDate 2023-05-04 22:34:57
* @Entity com.fjr.entity.ShoppingCart
*/
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {

}





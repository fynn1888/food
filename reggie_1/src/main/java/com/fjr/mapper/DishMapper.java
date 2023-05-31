package com.fjr.mapper;

import com.fjr.entity.Dish;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 广理最靓的仔
* @description 针对表【dish(菜品管理)】的数据库操作Mapper
* @createDate 2023-03-27 16:19:42
* @Entity com.fjr.entity.Dish
*/
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

}





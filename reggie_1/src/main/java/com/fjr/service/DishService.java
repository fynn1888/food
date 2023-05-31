package com.fjr.service;

import com.fjr.dto.DishDto;
import com.fjr.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 广理最靓的仔
* @description 针对表【dish(菜品管理)】的数据库操作Service
* @createDate 2023-03-27 16:19:42
*/
public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDto dishDto);
    public DishDto getByIdWithFlavors(Long id);

    void updateWithFlavor(DishDto dishDto);
}

package com.fjr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fjr.common.CustomException;
import com.fjr.entity.Category;
import com.fjr.entity.Dish;
import com.fjr.entity.Setmeal;
import com.fjr.service.CategoryService;
import com.fjr.mapper.CategoryMapper;
import com.fjr.service.DishService;
import com.fjr.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author 广理最靓的仔
* @description 针对表【category(菜品及套餐分类)】的数据库操作Service实现
* @createDate 2023-03-26 16:41:51
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        dishLambdaQueryWrapper.eq(Dish::getIsDeleted,0);
        //查看分类下有没有菜品
        int count1 = dishService.count(dishLambdaQueryWrapper);
        //判断有无
        if(count1>0){
            throw new CustomException("当前分类下有菜品，无法删除");
        }
        //添加条件
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        setmealLambdaQueryWrapper.eq(Setmeal::getIsDeleted,0);
        //查看套餐下有无菜品
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2>0){
            throw new CustomException("当前套餐下有菜品，无法删除");
        }
        //如果没有则正常删除
        super.removeById(id);
    }
}





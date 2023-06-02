package com.fjr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fjr.dto.SetmealDto;
import com.fjr.entity.Setmeal;
import com.fjr.entity.SetmealDish;
import com.fjr.service.SetmealDishService;
import com.fjr.service.SetmealService;
import com.fjr.mapper.SetmealMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author 广理最靓的仔
* @description 针对表【setmeal(套餐)】的数据库操作Service实现
* @createDate 2023-03-27 16:20:02
*/
@Transactional
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
    implements SetmealService{
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //先将套餐基本信息添加
        this.save(setmealDto);
        //再获取菜品的详细集合
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //将集合中的套餐id设置
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId().toString());
            return item;
        }).collect(Collectors.toList());
        //这里debug发现其实stream流结束后是会将原集合数据更改掉，所以不需要再新建一个集合来接收
        setmealDishService.saveBatch(setmealDishes);
        //将redis内的数据精确清空
        String key="setmeal"+setmealDto.getCategoryId()+"_"+setmealDto.getStatus();
        stringRedisTemplate.delete(key);
    }

    @Override
    public SetmealDto getSetmealWithDish(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        BeanUtils.copyProperties(setmeal,setmealDto);
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        List<SetmealDish> list = setmealDto.getSetmealDishes();
        String id = setmealDto.getId().toString();
        list.stream().map((item)->{
            item.setSetmealId(id);
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(list);
        //将redis内的数据精确清空
        String key="setmeal"+setmealDto.getCategoryId()+"_"+setmealDto.getStatus();
        stringRedisTemplate.delete(key);
    }
}





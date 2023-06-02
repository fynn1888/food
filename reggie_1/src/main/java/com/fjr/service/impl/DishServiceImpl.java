package com.fjr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fjr.dto.DishDto;
import com.fjr.entity.Dish;
import com.fjr.entity.DishFlavor;
import com.fjr.service.DishFlavorService;
import com.fjr.service.DishService;
import com.fjr.mapper.DishMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author 广理最靓的仔
* @description 针对表【dish(菜品管理)】的数据库操作Service实现
* @createDate 2023-03-27 16:19:42
*/
@Service
//开启事务，启动类要开启@EnableTransactionManagement
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
    implements DishService{
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //先将dish表的数据写入
        this.save(dishDto);

        //获取到口味flavors集合
        List<DishFlavor> flavors = dishDto.getFlavors();
        /**
         * 使用java8新特性stream流和lambda表达式
         * map表将集合做处理，(item)理解为变量或方法，->后即为方法体
         * 返回变量item（已做处理）
         * 最后将map再转回list
         * 再将最终的结果重新赋给flavors
         * 这里是要将集合的id设置为菜品id
         */
        flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
        //将redis内的数据精确清空
        String key="dish"+dishDto.getCategoryId()+"_"+dishDto.getStatus();
        stringRedisTemplate.delete(key);
    }

    @Override
    public DishDto getByIdWithFlavors(Long id) {
        //先获取到dish的数据
        Dish dish = this.getById(id);
        //创建DishFlavor的查询对象
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        //通过菜品id查找对应的口味
        queryWrapper.eq(DishFlavor::getDishId,id);
        //使用list方法返回一个集合
        List<DishFlavor> dishFlavor = dishFlavorService.list(queryWrapper);
        //新建一个dishDto对象来接收口味数据
        DishDto dishDto = new DishDto();
        dishDto.setFlavors(dishFlavor);
        //再将dish数据复制给dishDto
        BeanUtils.copyProperties(dish,dishDto);
        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //先将dish表修改
        this.updateById(dishDto);
        //再将flavors表修改，先把原来的删掉再重新insert
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //获取到口味flavors集合
        List<DishFlavor> flavors = dishDto.getFlavors();
        /**
         * 使用java8新特性stream流和lambda表达式
         * map表将集合做处理，(item)理解为变量或方法，->后即为方法体
         * 返回变量item（已做处理）
         * 最后将map再转回list
         * 再将最终的结果重新赋给flavors
         * 这里是要将集合的id设置为菜品id
         */
        flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
        //将redis内的数据精确清空
        String key="dish"+dishDto.getCategoryId()+"_"+dishDto.getStatus();
        stringRedisTemplate.delete(key);
    }
}





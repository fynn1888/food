package com.fjr.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fjr.common.R;
import com.fjr.dto.DishDto;
import com.fjr.entity.Category;
import com.fjr.entity.Dish;
import com.fjr.entity.DishFlavor;
import com.fjr.service.CategoryService;
import com.fjr.service.DishFlavorService;
import com.fjr.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 新增菜品，自定义了service层，新加了一个两张表的实体类，菜品和口味
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("新增成功");
    }

    /**
     * 菜品分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //创建dish的分页对象
        Page<Dish> dishPage = new Page<>(page, pageSize);
        //创建dishDto的分页对象
        Page<DishDto> dtoPage = new Page<>(page, pageSize);
        //查询出dish
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Dish::getSort);
        queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        dishService.page(dishPage,queryWrapper);
        //将查询结果除了菜品集合其他属性克隆给dishDto的分页对象
        BeanUtils.copyProperties(dishPage,dtoPage,"records");
        //获取到菜品集合
        List<Dish> records = dishPage.getRecords();
        //新建一个集合接收处理之后的数据
        List<DishDto> list=null;
//    将集合处理，设置categoryName，这里先使用流的filter方法将被逻辑删除的菜去掉，详细查阅java8新特性stream流api
        list=records.stream().filter(item->item.getIsDeleted()!=1
        ).map((item)->{
            //新建一个dishDto来装循环出的每一个菜品，然后通过getCategoryId将categoryName查出来然后set进dishDto
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    /**
     * 修改页面回显数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavors(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改成功");
    }

    /**
     * 批量修改
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable int status,@RequestParam List<Long> ids){
        //用获取到的id数组查出要改的实体
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        List<Dish> list = dishService.list(queryWrapper);
        ArrayList<Object> keys = new ArrayList<>();
        //使用stream流将status属性更改，然后再改数据库
        List<Dish> dishList = list.stream().map((item) -> {
            String key="dish"+item.getCategoryId()+"_"+1;
            if (!(item.getStatus() == status)) {
                if (!(keys.contains(key))){
                    keys.add(key);
                }
                item.setStatus(status);
            }
            return item;
        }).collect(Collectors.toList());
        dishService.updateBatchById(dishList);
        //将更新了状态的菜品数据从redis中删除
        for (Object key : keys) {
            stringRedisTemplate.delete((String) key);
        }
        return R.success("修改成功");
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        //用获取到的id数组查出要改的实体
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        List<Dish> list = dishService.list(queryWrapper);
        //使用stream流将IsDeleted属性更改，然后再改数据库
        List<Dish> dishList = list.stream().map((item) -> {
            if (!(item.getIsDeleted() == 1)) {
                item.setIsDeleted(1);
            }
            return item;
        }).collect(Collectors.toList());
        dishService.updateBatchById(dishList);

        return R.success("删除成功");
    }

    /**
     * 将菜品数据查询回显
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //先从redis中取数据，如果为空就查数据库,构造动态的key,将每个分类分别存入redis
        String key="dish"+dish.getCategoryId()+"_"+dish.getStatus();
        List<DishDto> dtoList=null;
        String s = stringRedisTemplate.opsForValue().get(key);
        dtoList= (List<DishDto>) JSONArray.parse(s);
        if (dtoList!=null){
            return R.success(dtoList);
        }
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //如果是不确定有没有的条件就要添加一个判断是否为空
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        queryWrapper.like(StringUtils.isNotEmpty(dish.getName()),Dish::getName,dish.getName());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.eq(Dish::getIsDeleted,0);
        List<Dish> list = dishService.list(queryWrapper);
        //将集合处理，设置categoryName，这里先使用流的filter方法将被逻辑删除的菜去掉，详细查阅java8新特性stream流api
        dtoList=list.stream().filter(item->item.getIsDeleted()!=1
        ).map((item)->{
            //新建一个dishDto来装循环出的每一个菜品，然后查出菜的口味set进dto
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper1);
            if (dishFlavors!=null){
                dishDto.setFlavors(dishFlavors);
            }
            return dishDto;
        }).collect(Collectors.toList());
        //首次查将数据存入redis
        String s1 = JSON.toJSONString(dtoList);
        stringRedisTemplate.opsForValue().set(key,s1,1, TimeUnit.HOURS);
        return R.success(dtoList);
    }
}

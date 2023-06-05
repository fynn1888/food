package com.fjr.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fjr.common.R;
import com.fjr.dto.SetmealDto;
import com.fjr.entity.Category;
import com.fjr.entity.Setmeal;
import com.fjr.service.CategoryService;
import com.fjr.service.SetmealDishService;
import com.fjr.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("新增成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //创建setmeal分页对象,先查出基础信息
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        setmealService.page(setmealPage, queryWrapper);
        //将records拿出来一个一个赋给dto对象，再通过id查询套餐分类名称将结果set进dto对象
        Page<SetmealDto> dtoPage = new Page<>(page, pageSize);
        BeanUtils.copyProperties(setmealPage, dtoPage, "records");
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> list = records.stream().filter(
                item -> item.getIsDeleted() != 1).map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            Category byId = categoryService.getById(item.getCategoryId());
            if (!(byId == null)) {
                BeanUtils.copyProperties(item, setmealDto);
                setmealDto.setCategoryName(byId.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setMealCache",allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        List<Setmeal> list = setmealService.list(queryWrapper);
        //将查出的删除属性修改
        list.stream().map((item)->{
            if (item.getIsDeleted()!=1){
                item.setIsDeleted(1);
            }
            return item;
        }).collect(Collectors.toList());
        setmealService.updateBatchById(list);
        return R.success("删除成功");
    }

    /**
     * 批量修改状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    @CacheEvict(value = "setMealCache",allEntries = true)
    public R<String> updateStatus(@PathVariable int status,@RequestParam List<Long> ids){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        List<Setmeal> list = setmealService.list(queryWrapper);
//        ArrayList<Object> keys = new ArrayList<>();
        list.stream().map((item)->{
//            String key="setMealCache::"+item.getCategoryId();
            if (item.getStatus()!=status){
//                if (!(keys.contains(key))){
//                    keys.add(key);
//                }
                item.setStatus(status);
            }
            return item;
        }).collect(Collectors.toList());
        setmealService.updateBatchById(list);
        //将更新了状态的菜品数据从redis中删除
//        for (Object key : keys) {
//            stringRedisTemplate.delete((String) key);
//        }
        return R.success("状态修改成功");
    }

    /**
     * 修改套餐的回显数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> querySetmealById(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getSetmealWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    @CacheEvict(value = "setMealCache",key = "#setmealDto.categoryId")
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("修改成功");
    }

    @GetMapping("/list")
    @Cacheable(value = "setMealCache",key = "#setmeal.categoryId")
    public R<List<Setmeal>> list(Setmeal setmeal){
//        //先从redis中取数据，如果为空就查数据库,构造动态的key,将每个分类分别存入redis
//        String key="setmeal"+setmeal.getCategoryId()+"_"+setmeal.getStatus();
        List<Setmeal> dtoList=null;
//        String s = stringRedisTemplate.opsForValue().get(key);
//        dtoList= (List<Setmeal>) JSONArray.parse(s);
//        if (dtoList!=null){
//            return R.success(dtoList);
//        }
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(Setmeal::getStatus,1);
        queryWrapper.eq(Setmeal::getIsDeleted,0);
        List<Setmeal> list = setmealService.list(queryWrapper);
//        //首次查将数据存入redis
//        String s1 = JSON.toJSONString(dtoList);
//        stringRedisTemplate.opsForValue().set(key,s1,1, TimeUnit.HOURS);
        return R.success(list);
    }
}

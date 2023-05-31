package com.fjr.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fjr.common.R;
import com.fjr.entity.Category;
import com.fjr.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品分类管理
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CatefgoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品分类、套餐分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("新增菜品分类");
        categoryService.save(category);
        return R.success("新增成功");
    }

    /**
     * 分页查询菜品、套餐
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        //构造分页对象
        Page pageInfo = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.orderByAsc(Category::getSort);
        //执行查询
        categoryService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 修改分类
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 删除分类
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") Long id){
        categoryService.remove(id);
        return R.success("删除成功");
    }

    /**
     * 分类下拉表回显数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加按照type查询条件
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加按照第一顺序sort，第二顺序更新时间（升序）查询
        queryWrapper.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}

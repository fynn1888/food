package com.fjr.service;

import com.fjr.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 广理最靓的仔
* @description 针对表【category(菜品及套餐分类)】的数据库操作Service
* @createDate 2023-03-26 16:41:51
*/
public interface CategoryService extends IService<Category> {
    public void remove(Long id);

}

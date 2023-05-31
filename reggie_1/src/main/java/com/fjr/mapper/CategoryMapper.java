package com.fjr.mapper;

import com.fjr.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 广理最靓的仔
* @description 针对表【category(菜品及套餐分类)】的数据库操作Mapper
* @createDate 2023-03-26 16:41:51
* @Entity com.fjr.entity.Category
*/
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}





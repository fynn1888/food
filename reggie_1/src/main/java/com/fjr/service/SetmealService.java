package com.fjr.service;

import com.fjr.dto.SetmealDto;
import com.fjr.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 广理最靓的仔
* @description 针对表【setmeal(套餐)】的数据库操作Service
* @createDate 2023-03-27 16:20:02
*/
public interface SetmealService extends IService<Setmeal> {

    public void saveWithDish(SetmealDto setmealDto);

    public SetmealDto getSetmealWithDish(Long id);

    public void updateWithDish(SetmealDto setmealDto);
}

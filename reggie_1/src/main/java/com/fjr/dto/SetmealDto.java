package com.fjr.dto;

import com.fjr.entity.Setmeal;
import com.fjr.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {
    private List<SetmealDish> setmealDishes;
    private String categoryName;
}

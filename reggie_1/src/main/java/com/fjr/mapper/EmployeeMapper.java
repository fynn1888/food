package com.fjr.mapper;

import com.fjr.entity.Employee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 广理最靓的仔
* @description 针对表【employee(员工信息)】的数据库操作Mapper
* @createDate 2023-01-29 15:13:28
* @Entity com.fjr.entity.Employee
*/
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}





package com.fjr.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fjr.entity.Employee;
import com.fjr.service.EmployeeService;
import com.fjr.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;

/**
* @author 广理最靓的仔
* @description 针对表【employee(员工信息)】的数据库操作Service实现
* @createDate 2023-01-29 15:13:28
*/
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>
    implements EmployeeService{

}





package com.fjr.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fjr.common.R;
import com.fjr.entity.Employee;
import com.fjr.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 登录功能
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //将密码md5加密
        String password = employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());

        //根据页面提交的用户名查询
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //是否查到
        if (emp==null){
            return R.error("查无此账户");
        }

        //判断密码是否正确
        if (!((emp.getPassword()).equals(password))){
            return R.error("密码错误");
        }

        //判断状态
        if (emp.getStatus()==0){
            return R.error("没有权限登录");
        }

        //全部pass就将id设置session域，并返回正确数据
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("quit");
    }

    /**
     * 添加员工
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> addemp(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());
        //将employee信息设置完整后添加进数据库，密码要记得加密后存入，这里统一在新增员工时给出默认密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        //获取当前操作人信息
//        Long empid= (Long) request.getSession().getAttribute("employee");
//        //设置创建时间更新时间创建人更新人
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setCreateUser(empid);
//        employee.setUpdateUser(empid);
        //调用mybatis-plus方法新增员工
        employeeService.save(employee);
        //操作成功返回信息
        return R.success("success");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        Page  pageinfo= new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加模糊查询条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行分页查询
        employeeService.page(pageinfo,queryWrapper);
        //返回分页对象
        return R.success(pageinfo);
    }

    /**
     * 修改员工信息功能
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        Long employee1 = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(employee1);
        employeeService.updateById(employee);
        return R.success("修改成功");
    }

    /**
     * 数据回显功能
     * @param id
     * @return
     */
    //通过url传参注释加参数获取给传入参数
    @GetMapping("/{id}")
    //@PathVariable后面参数为url传来的参数
    public R<Employee> getById (@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }
}

package com.fjr.filter;


import com.alibaba.fastjson.JSON;

import com.fjr.common.BaseContext;
import com.fjr.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//添加注解使此类为过滤器类，同时springboot启动类需要添加注解@ServletComponentScan这样才能扫描到此类
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
//开启日志功能（lombok）
@Slf4j
public class LoginCheckFilter implements Filter {
    //使用工具AntPathMatcher匹配器，可以匹配通配符**
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取当前请求地址
        String uri=request.getRequestURI();
        log.info("{}",uri);
        //设置直接放行的网址（这里注意资源前都要加/，在写的时候就因为忘记给静态资源加导致访问直接json数据糊脸）
        String urls[]={
                "/backend/**",
                "/front/**",
                "/employee/login",
                "/employee/logout",
                "/user/sendMsg",
                "/user/login"

        };


        //判断网址是否直接放行
        boolean check = check(urls, uri);
        if (check){
            log.info("本次拦截不需要操作：{}",uri);
            filterChain.doFilter(request,response);
            return;
        }


            //判断用戶是否已登录
            if (request.getSession().getAttribute("user")!=null){
                log.info("用户已登录，id为：{}",request.getSession().getAttribute("user"));
                Long userId = (Long) request.getSession().getAttribute("user");
                //获取登录id设置进ThreadLocal
                BaseContext.setCurrentId(userId);
                filterChain.doFilter(request,response);
                return;
            }



            //判断管理員是否已登录
            if (request.getSession().getAttribute("employee")!=null){
                log.info("管理员已登录，id为：{}",request.getSession().getAttribute("employee"));
                Long empId = (Long) request.getSession().getAttribute("employee");
                //获取登录id设置进ThreadLocal
                BaseContext.setCurrentId(empId);
                filterChain.doFilter(request,response);
                return;

        }


            log.info("未登录");
        //如果未登录，根据前端js需要返回json数据“NOTLOGIN”前端会跳转到登录页面
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            return;
    }
    //匹配路径方法，使用匹配器进行匹配，匹配上返回真值
    public boolean check(String[] urls,String uri){
        for (String url: urls) {
            boolean match = PATH_MATCHER.match(url, uri);
            if (match){
                return true;
            }
        }
        return false;
    }
}

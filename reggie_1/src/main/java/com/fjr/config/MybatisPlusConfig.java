package com.fjr.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//开启配置类声明
@Configuration
public class MybatisPlusConfig {
    //将mp提供的分页插件添加进容器管理
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        //新建一个mp拦截器，将分页功能加入拦截器
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusInterceptor;
    }
}

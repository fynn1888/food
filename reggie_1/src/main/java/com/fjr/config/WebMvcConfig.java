package com.fjr.config;


import com.fjr.common.JacksonObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;
//mvc扩展功能配置类
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    //静态资源映射
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    //mvc提供的消息转换组件
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换对象
        MappingJackson2HttpMessageConverter converter=new MappingJackson2HttpMessageConverter();
        //JacksonObjectMapper类为下载的自定义方法类，此行为设置转换规则
        converter.setObjectMapper(new JacksonObjectMapper());
        //将转换对象加入容器当中，并置为第一位生效
        converters.add(0,converter);
    }
}

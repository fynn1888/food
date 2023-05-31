package com.fjr.common;

import lombok.extern.slf4j.Slf4j;

/**
 * 自定义异常类
 */
@Slf4j
public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
        log.info("自定义异常类...");
    }
}

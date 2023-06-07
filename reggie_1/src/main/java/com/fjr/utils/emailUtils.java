package com.fjr.utils;

import org.apache.commons.mail.EmailException;


import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Value;

public class emailUtils {
    public static void sendAuthCodeEmail(String email, String authCode) {
        try {
            SimpleEmail mail = new SimpleEmail();
            mail.setHostName("smtp.qq.com");//发送邮件的服务器,这个是qq邮箱的，不用修改
            mail.setAuthentication("1453917871@qq.com", "gwpcoihxqclvbaae");//第一个参数是对应的邮箱用户名一般就是自己的邮箱第二个参数就是SMTP的密码,我们上面获取过了
            mail.setFrom("1453917871@qq.com","fjr");  //发送邮件的邮箱和发件人
            mail.setSSLOnConnect(false); //使用安全链接
            mail.setSmtpPort(587);
            mail.addTo(email);//接收的邮箱
            mail.setSubject("验证码");//设置邮件的主题
            mail.setContent("您的验证码为:"+authCode+"(一分钟内有效)","text/html");//设置邮件的内容
            mail.setCharset("UTF-8");
            mail.send();//发送
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }
}

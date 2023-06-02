package com.fjr.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fjr.common.R;
import com.fjr.entity.User;
import com.fjr.service.UserService;
import com.fjr.utils.ValidateCodeUtils;
import com.fjr.utils.emailUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取邮箱地址
        String email = user.getPhone();
        //生成验证码
        if (StringUtils.isNotEmpty(email)){
            String code = ValidateCodeUtils.generateValidateCode4String(4);
            log.info("验证码为"+code);
            //邮箱发送验证码，并将验证码存入session
            emailUtils.sendAuthCodeEmail(email,code);
//            session.setAttribute("code",code);
            //将验证码存入redis,时间设为5分钟
            stringRedisTemplate.opsForValue().set("code",code,5, TimeUnit.MINUTES);
            return R.success("发送成功");
        }
        return R.error("发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession session){
        //获取邮箱地址以及验证码
        String email = map.get("phone").toString();
        String code = map.get("code").toString();
        //从session中取先保存的验证码进行校验
//        Object code1 = session.getAttribute("code");
        //从redis中取出验证码
        Object code1 = stringRedisTemplate.opsForValue().get("code");
        //通过就进行查询，如果查询为空则是新用户则直接注册进
        if (code1!=null&&code1.equals(code)){
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,email);
            User user = userService.getOne(queryWrapper);
            if (user==null){
                user = new User();
                user.setStatus(1);
                user.setPhone(email);
                user.setName("新用户"+email);
                userService.save(user);
            }
            //将用户信息存入session，并返回用户信息
            session.setAttribute("user",user.getId());
            //将验证码从redis中删除
            stringRedisTemplate.delete("code");
            return R.success(user);
        }
        return R.error("登录失败");
    }
}

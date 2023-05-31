package com.fjr.mapper;

import com.fjr.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 广理最靓的仔
* @description 针对表【user(用户信息)】的数据库操作Mapper
* @createDate 2023-04-12 22:28:06
* @Entity com.fjr.entity.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}





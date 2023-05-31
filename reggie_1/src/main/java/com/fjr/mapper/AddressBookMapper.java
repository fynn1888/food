package com.fjr.mapper;

import com.fjr.entity.AddressBook;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 广理最靓的仔
* @description 针对表【address_book(地址管理)】的数据库操作Mapper
* @createDate 2023-04-21 15:14:59
* @Entity com.fjr.entity.AddressBook
*/
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {

}





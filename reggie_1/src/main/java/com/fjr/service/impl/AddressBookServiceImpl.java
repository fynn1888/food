package com.fjr.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fjr.entity.AddressBook;
import com.fjr.service.AddressBookService;
import com.fjr.mapper.AddressBookMapper;
import org.springframework.stereotype.Service;

/**
* @author 广理最靓的仔
* @description 针对表【address_book(地址管理)】的数据库操作Service实现
* @createDate 2023-04-21 15:14:59
*/
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>
    implements AddressBookService{

}





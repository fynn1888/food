package com.fjr.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fjr.common.BaseContext;
import com.fjr.common.R;
import com.fjr.entity.AddressBook;
import com.fjr.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 地址列表
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(){
        //通过线程获取当前用户id，查询该用户下的所有地址
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,currentId);
        List<AddressBook> list = addressBookService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook){
        //将当前用户id设置进实体，判断当前用户是否有默认地址如果没有就将现在这个地址设为默认地址并新增
        Long currentId = BaseContext.getCurrentId();
        addressBook.setUserId(currentId);
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,currentId);
        queryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook one = addressBookService.getOne(queryWrapper);
        if (one!=null){
            addressBookService.save(addressBook);
        }else {
            addressBook.setIsDefault(1);
            addressBookService.save(addressBook);
        }

        return R.success("新增成功");
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<String> is_default(@RequestBody AddressBook addressBook){
        //使用LambdaUpdateWrapper对象对有同一属性值的多条数据修改
        log.info(addressBook.toString());
        Long currentId = BaseContext.getCurrentId();
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        //添加修改默认属性条件
        updateWrapper.set(AddressBook::getIsDefault,0);
        updateWrapper.eq(AddressBook::getUserId,currentId);
        addressBookService.update(updateWrapper);
        //将实体数据完善然后修改
        addressBook.setUserId(currentId);
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success("设置成功");
    }

    /**
     * 修改界面数据回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        return R.success(addressBook);
    }

    /**
     * 修改地址
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }

    /**
     * 删除地址
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam Long ids){
        addressBookService.removeById(ids);
        return R.success("删除成功");
    }

    @GetMapping("/default")
    public R<AddressBook> get(){
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        return R.success(addressBook);
    }
}

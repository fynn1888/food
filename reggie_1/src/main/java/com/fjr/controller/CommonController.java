package com.fjr.controller;


import com.fjr.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {
    //将文件存放路径写进配置文件，引用配置文件数据
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 上传文件至服务器
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //获取原文件名
        String originalFilename = file.getOriginalFilename();
        //获取后缀名
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        //用uuid生成随机文件名
        String fileName = UUID.randomUUID().toString()+substring;
        //做判断如果没有该文件就新创一个
        File file1 = new File(basePath);
        if (!(file1.exists())){
            file1.mkdir();
        }
        try {
            //将获取的文件转存
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(fileName);
    }

    /**
     * 从服务器下载文件至浏览器
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        //设置响应格式
        response.setContentType("image/jpeg");
        //获取文件输入流
        try {
            FileInputStream fileInputStream = new FileInputStream(basePath+name);
            //获取response输出流
            ServletOutputStream outputStream = response.getOutputStream();
            //边读边写
            int len=0;
            byte[] bytes = new byte[1024];
            //等于-1就是读到了文件末尾
            while ((len=fileInputStream.read(bytes))!=-1){
            //从头写到尾
                outputStream.write(bytes,0,len);
            //百度说读写的时候有缓冲，这里的作用是将缓冲区的数据强制写出
                outputStream.flush();
            }
            fileInputStream.close();
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

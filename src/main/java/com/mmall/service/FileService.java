package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件有关类
 * Created By Cx On 2018/9/1 23:25
 */
public interface FileService {
    /**
     * 将文件存到path路径下
     * 返回存到服务器的文件的文件名
     */
    String upload(MultipartFile file, String path);
}

package com.mmall.service.impl;

import com.mmall.enums.ResultEnum;
import com.mmall.exception.SellException;
import com.mmall.service.FileService;
import com.mmall.utils.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created By Cx On 2018/9/1 23:25
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class FileServiceImpl implements FileService {
    @Override
    public String upload(MultipartFile file, String path) {
        //获取文件名
        String fileName = file.getOriginalFilename();
        String fileExtensionName;
        if (fileName != null && fileName.contains(".")){
            //获取扩展名，如：a.jpg  则jpg即为扩展名.    Extension:延展
            fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        }else {
            log.error("【上传文件】上传文件失败，无法获取文件扩展名，fileName={}",fileName);
            throw new SellException(ResultEnum.ERROR.getCode(),"文件名格式有误，无法获取文件扩展名");
        }
        //为防止a、b不同用户上传同名文件被覆盖的情况，使用UUID生成文件名前缀
        String uploadFileName = UUID.randomUUID().toString().concat(".").concat(fileExtensionName);
        File fileDir = new File(path);
        if (!fileDir.exists()){
            //如果路径不存在
            //将用户权限设为可写，并创建路径
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path,uploadFileName);
        try {
            //上传文件到工作目录
            file.transferTo(targetFile);
            //上传文件到FTP服务
           if ( !FTPUtil.uploadFile(targetFile)){
               log.error("[上传文件]文件上传失败，ftp服务器连接失败");
               throw new SellException(ResultEnum.ERROR.getCode(),"服务器连接失败");
           }
            //删除工作目录的文件
            targetFile.delete();
        } catch (IOException e) {
            log.error("[上传文件]文件上传失败，e={}",e);
            throw new SellException(ResultEnum.ERROR.getCode(),"文件上传失败");
        }
        return targetFile.getName();
    }
}

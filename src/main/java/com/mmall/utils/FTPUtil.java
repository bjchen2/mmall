package com.mmall.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * ftp工具类,用于上传文件
 * Created By Cx On 2018/9/2 22:31
 */
@Data
@Slf4j
public class FTPUtil {

    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public FTPUtil(){
        this.ip = PropertiesUtil.getProperty("ftp.server.ip");
        this.port = 21;
        this.user = PropertiesUtil.getProperty("ftp.user");
        this.pwd = PropertiesUtil.getProperty("ftp.pass");
        this.ftpClient = new FTPClient();
    }

    public static Boolean uploadFile(File file) throws IOException {
        FTPUtil ftpUtil = new FTPUtil();
        //将文件上传到ftp文件夹的img文件夹下
        return ftpUtil.upload(file);
    }

    private Boolean upload(File file) throws IOException {
        FileInputStream fis = null;
        if (connectServer()){
            try {
                //修改工作路径到remotePath
                ftpClient.changeWorkingDirectory("img");
                //设置缓冲区大小 - 1M
                ftpClient.setBufferSize(1024);
                //设置文件类型为二进制文件
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                //设置编码为为UTF-8
                ftpClient.setControlEncoding("UTF-8");
                //开启被动传输
                ftpClient.enterLocalPassiveMode();
                fis = new FileInputStream(file);
                //上传文件
                ftpClient.storeFile(file.getName(),fis);
            } catch (IOException e) {
                log.error("[上传文件]上传失败");
                return false;
            }finally {
                if (fis != null){
                    fis.close();
                }
                //关闭ftp连接
                ftpClient.disconnect();
            }
        }
        return true;
    }

    private Boolean connectServer(){
        try {
            ftpClient.connect(ip);
            return ftpClient.login(user,pwd);
        } catch (IOException e) {
            log.error("[连接FTP服务器]连接失败");
            return false;
        }
    }

}

package com.mmall.config;


import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created By Cx On 2018/9/11 22:41
 */
public class AlipayConfig {

    private static Properties configs;

    public static String sellerId;

    public static String openApiDomain;   // 支付宝openapi域名
    public static String mcloudApiDomain;  // 支付宝mcloudmonitor域名
    public static String pid;             // 商户partner id
    public static String appid;           // 商户应用id

    public static String format;    //参数返回格式，只支持json
    public static String charset;    //	请求和签名使用的字符编码格式，支持GBK和UTF-8

    public static String privateKey;      // RSA私钥，用于对商户请求报文加签
    public static String publicKey;       // RSA公钥，仅用于验证开发者网关
    public static String alipayPublicKey; // 支付宝RSA公钥，用于验签支付宝应答
    public static String signType;     // 签名类型

    public static int maxQueryRetry;   // 最大查询次数
    public static long queryDuration;  // 查询间隔（毫秒）

    public static int maxCancelRetry;  // 最大撤销次数
    public static long cancelDuration; // 撤销间隔（毫秒）

    public static long heartbeatDelay ; // 交易保障线程第一次调度延迟（秒）
    public static long heartbeatDuration ; // 交易保障线程调度间隔（秒）

    // 根据文件名读取配置文件，文件后缀名必须为.properties
    public synchronized static void init(String filePath) {
        if (configs != null) {
            return;
        }

        try {
            configs = new Properties();
            configs.load(new FileReader(filePath));
        } catch (IOException e) {
            throw new IllegalStateException("can`t find file by path:" + filePath);
        }

        sellerId = configs.getProperty("seller_id");

        openApiDomain = configs.getProperty("open_api_domain");
        mcloudApiDomain = configs.getProperty("mcloud_api_domain");

        pid = configs.getProperty("pid");
        appid = configs.getProperty("appid");

        format = configs.getProperty("format");
        charset = configs.getProperty("charset");

        // RSA
        privateKey = configs.getProperty("private_key");
        publicKey = configs.getProperty("public_key");
        alipayPublicKey = configs.getProperty("alipay_public_key");
        signType = configs.getProperty("sign_type");

        // 查询参数
        maxQueryRetry = Integer.valueOf(configs.getProperty("max_query_retry"));
        queryDuration = Long.valueOf(configs.getProperty("query_duration"));
        maxCancelRetry = Integer.valueOf(configs.getProperty("max_cancel_retry"));
        cancelDuration = Long.valueOf(configs.getProperty("cancel_duration"));

        // 交易保障调度线程
        heartbeatDelay = Long.valueOf(configs.getProperty("heartbeat_delay"));
        heartbeatDuration = Long.valueOf(configs.getProperty("heartbeat_duration"));
    }

}

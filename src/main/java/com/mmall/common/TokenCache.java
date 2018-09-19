package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 通过guava工具包将令牌存放在本地缓存中（存在redis中更好，该处为了练习guava工具类的使用）
 * Created By Cx On 2018/8/26 16:58
 */
@Slf4j
public class TokenCache {
    /**
     * initialCapacity:为初始容量
     * maximumSize：最大容量，当超过最大容量时，会使用LRU（最近最少使用算法）清除
     * expireAfterAccess: 有效期
     */
    private static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000)
            .expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, String>() {
                //默认数据加载实现，当调用get取值时没有对应value，则调用该方法进行加载
                @Override
                public String load(String s) {
                    return null;
                }
            });

    public static void setValue(String key,String value){
        localCache.put(key,value);
    }

    public static String getKey(String key){
        try {
            return localCache.get(key);
        } catch (ExecutionException e) {
            log.error("localCache get error",e);
        }
        return null;
    }
}

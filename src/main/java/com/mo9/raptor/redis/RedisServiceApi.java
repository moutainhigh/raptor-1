package com.mo9.raptor.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author jyou
 * Redis 缓存的基础服务，提供了手动操作控制redis缓存的途径
 */
@Service
public class RedisServiceApi {

    private static Logger logger = LoggerFactory.getLogger(RedisServiceApi.class);

    /**
     * 根据key读取缓存
     * @param key
     * @return
     */
    public Object get(final String key, RedisTemplate redisTemplate) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }

    /**
     * 写入缓存
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value, RedisTemplate redisTemplate) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            logger.error("Redis缓存存储异常，key：{}，value：{}，exception：{}", key, value, e.getMessage());
        }
        return result;
    }

    /**
     * 写入缓存并设置过期时间
     * @param key
     * @param value
     * @param expireTime
     * @return
     */
    public boolean set(final String key, Object value, Long expireTime, RedisTemplate redisTemplate) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            logger.error("Redis缓存存储异常，key：{}，value：{}，expireTime：{}，exception：{}", key, value, expireTime, e.getMessage());
        }
        return result;
    }

    /**
     * 判断缓存中是否有key对应的value
     * @param key
     * @return
     */
    public boolean exists(final String key, RedisTemplate redisTemplate) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 充值key的过期时间
     * @param key
     * @param expireTime
     * @param redisTemplate
     * @return
     */
    public boolean expireSeconds(final String key,Long expireTime, RedisTemplate redisTemplate) {
       return redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }



    /**
     * 获取key剩余时间(秒)
     * @param key
     * @param redisTemplate
     * @return
     */
    public long getExpireSeconds(String key,RedisTemplate redisTemplate){
        return redisTemplate.getExpire(key,TimeUnit.SECONDS);
    }
    /**
     * 删除对应key的value
     * @param key
     */
    public void remove(final String key, RedisTemplate redisTemplate) {
        if (redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 批量删除正则表达式对应key的value
     * @param pattern
     */
    public void removePattern(final String pattern, RedisTemplate redisTemplate) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 缓存哈希表
     * @param key 一个key对应一张哈希表
     * @param hashKey 一个hashKey对应一个value
     * @param value
     */
    public void hSet(String key, Object hashKey, Object value, RedisTemplate redisTemplate){
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.put(key,hashKey,value);
    }

    /**
     * 哈希获取数据
     * @param key
     * @param hashKey
     * @return
     */
    public Object hGet(String key, Object hashKey, RedisTemplate redisTemplate){
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.get(key,hashKey);
    }

    /**
     * 列表添加,压栈
     * @param k
     * @param v
     */
    public void lPush(String k, Object v, RedisTemplate redisTemplate){
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.leftPush(k,v);
    }

    /**
     * 列表获取，出栈
     * @param k
     * @return
     */
    public Object rPop(String k, RedisTemplate redisTemplate){
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.rightPop(k);
    }

    /**
     * 集合添加
     * @param key
     * @param value
     */
    public void add(String key, Object value, RedisTemplate redisTemplate){
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.add(key,value);
    }

    /**
     * 集合获取
     * @param key
     * @return
     */
    public Set<Object> setMembers(String key, RedisTemplate redisTemplate){
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        return set.members(key);
    }

    /**
     * 有序集合添加
     * @param key
     * @param value
     * @param scoure
     */
    public void zAdd(String key, Object value, double scoure, RedisTemplate redisTemplate){
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        zset.add(key,value,scoure);
    }

    /**
     * 有序集合获取
     * @param key
     * @param scoure
     * @param scoure1
     * @return
     */
    public Set<Object> rangeByScore(String key, double scoure, double scoure1, RedisTemplate redisTemplate){
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        return zset.rangeByScore(key, scoure, scoure1);
    }


    public Long increment(String key, Long value, RedisTemplate redisTemplate) {
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            return operations.increment(key, value);
        } catch (Exception e) {
            logger.error("Redis缓存存储异常，key：{}，value：{}，，exception：{}", key, value,e.getMessage());
        }
        return -1L;
    }
}

package cc.freecloudfx.littlemall.util;

import cc.freecloudfx.littlemall.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * @author StReaM on 3/19/2018
 */
@Slf4j /*using lombok*/
public class RedisPoolUtil {

    /* 根据源码，设置成功，返回1，失败返回0，返回值是 Long*/
    public static Long expire(String key, int expire/* 单位是秒 */) {
        Jedis jedis = null;
        Long result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.expire(key, expire);
        } catch (Exception e) {
            log.error("modify key expire: {} error", key, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    /* 带失效时间的 set, 对 session 操作很有用 */
    public static String setEx(String key, int expire/* 单位是秒 */, String val) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.setex(key, expire,val);
        } catch (Exception e) {
            log.error("setex key: {} value: {} error", key, val, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static String set(String key, String val) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.set(key, val);
        } catch (Exception e) {
            log.error("set key: {} value: {} error", key, val, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static String get(String key) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("set key: {} error", key, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static Long del(String key) {
        Jedis jedis = null;
        Long result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del key: {} error", key, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static void main(String[] args) {
        Jedis jedis = RedisPool.getJedis();

        RedisPoolUtil.set("sakura", "ayane");
        String value = RedisPoolUtil.get("sakura");
        RedisPoolUtil.setEx("Tda",1000,"Anai");
        RedisPoolUtil.expire("sakura", 2000);
        RedisPoolUtil.del("sakura");
    }

}

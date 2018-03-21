package cc.freecloudfx.littlemall.util;

import cc.freecloudfx.littlemall.common.RedisPool;
import cc.freecloudfx.littlemall.common.RedisShardedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

/**
 * @author StReaM on 3/19/2018
 */
@Slf4j /*using lombok*/
public class RedisShardedPoolUtil {

    /* 根据源码，设置成功，返回1，失败返回0，返回值是 Long*/
    public static Long expire(String key, int expire/* 单位是秒 */) {
        ShardedJedis jedis = null;
        Long result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.expire(key, expire);
        } catch (Exception e) {
            log.error("modify key expire: {} error", key, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    /* 带失效时间的 set, 对 session 操作很有用 */
    public static String setEx(String key, int expire/* 单位是秒 */, String val) {
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.setex(key, expire,val);
        } catch (Exception e) {
            log.error("setex key: {} value: {} error", key, val, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static String set(String key, String val) {
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.set(key, val);
        } catch (Exception e) {
            log.error("set key: {} value: {} error", key, val, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static String get(String key) {
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("set key: {} error", key, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static Long del(String key) {
        ShardedJedis jedis = null;
        Long result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del key: {} error", key, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static void main(String[] args) {
        Jedis jedis = RedisPool.getJedis();

        RedisShardedPoolUtil.set("sakura", "ayane");
        String value = RedisShardedPoolUtil.get("sakura");
        RedisShardedPoolUtil.setEx("Tda",1000,"Anai");
        RedisShardedPoolUtil.expire("sakura", 2000);
        RedisShardedPoolUtil.del("sakura");
    }

}

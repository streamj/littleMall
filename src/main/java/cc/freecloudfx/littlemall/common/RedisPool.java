package cc.freecloudfx.littlemall.common;

import cc.freecloudfx.littlemall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author StReaM on 3/19/2018
 */
public class RedisPool {

    private static JedisPool pool; // 保证启动时候就加载进来

    private static Integer maxTotal =
            Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20"));// 最大连接数
    private static Integer maxIdle =
            Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "10")); // 最大空闲连接实例
    private static Integer minIdle =
            Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "2"));  // 最小空闲
    private static Boolean testOnBorrow =
            Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow", "true"));// 是否验证可用性
    private static Boolean testOnReturn =
            Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return", "true"));// 归还时候是否验证

    private static String ip = PropertiesUtil.getProperty("redis.ip");
    private static Integer port = Integer.parseInt(PropertiesUtil.getProperty("redis.port"));

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        config.setBlockWhenExhausted(true); // 默认其实就是 true, 放在这里只是为了体现重要性

        pool = new JedisPool(config, ip, port, 1000 * 2);
    }

    static {
        initPool();
    }

    public static Jedis getJedis() {
        return pool.getResource();
    }

    public static void returnResource(Jedis jedis) {
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(Jedis jedis) {
        pool.returnBrokenResource(jedis);
    }

}

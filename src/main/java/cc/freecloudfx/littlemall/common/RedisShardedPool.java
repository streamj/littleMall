package cc.freecloudfx.littlemall.common;

import cc.freecloudfx.littlemall.util.PropertiesUtil;
import com.google.common.collect.Lists;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.List;

/**
 * @author StReaM on 3/21/2018
 */
public class RedisShardedPool {

    private static ShardedJedisPool pool; // 保证启动时候就加载进来

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

    private static String ip1 = PropertiesUtil.getProperty("redis1.ip");
    private static Integer port1 = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        config.setBlockWhenExhausted(true); // 默认其实就是 true, 放在这里只是为了体现重要性

        JedisShardInfo info = new JedisShardInfo(ip, port, 1000 * 2);
//        info.setPassword("password");
        JedisShardInfo info1 = new JedisShardInfo(ip1, port1, 1000 * 2);

        List<JedisShardInfo> jedisShardInfos = Lists.newArrayList();
        jedisShardInfos.add(info);
        jedisShardInfos.add(info1);

        pool = new ShardedJedisPool(config, jedisShardInfos, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);

    }

    static {
        initPool();
    }

    public static ShardedJedis getJedis() {
        return pool.getResource();
    }

    public static void returnResource(ShardedJedis jedis) {
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(ShardedJedis jedis) {
        pool.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        ShardedJedis shardedJedis = pool.getResource();

        for (int i = 0; i < 10; i++) {
            shardedJedis.set("key" + i, "value"+i);
        }
        returnResource(shardedJedis);
        System.out.println("end of program");
    }
}

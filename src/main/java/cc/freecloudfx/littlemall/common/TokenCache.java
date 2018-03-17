package cc.freecloudfx.littlemall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 使用 Guava 做的 TokenCache
 * @author StReaM on 2/27/2018
 */
public class TokenCache {

    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    public static final String TOKEN_PREFIX = "token_";

    private static LoadingCache<String, String> localCache = CacheBuilder.newBuilder()
            // todo 这里会吃屎，吃屎 google，吃屎 guava
            .initialCapacity(1000) // 缓存初始化容量
            .maximumSize(10000) // 缓存最大容量，超过这个，会用 LRU 移除一些容量
            .expireAfterAccess(12, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                // 默认的数据加载实现，当调用 get 的时候，如果 key 没有对应的值，就会调用这个方法
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });

    public static void setKey(String key, String value) {
        localCache.put(key, value);
    }

    public static String getKey(String key) {
        String value = null;
        try {
            value = localCache.get(key);
            if ("null".equals(value)) {
                return null;
            }
            return value;
        } catch (Exception e) {
            logger.error("Localcache get error", e);
        }
        return value;
    }
}

package com.houbank.utils;

import com.houbank.commons.serializer.Serializer;
import com.houbank.commons.serializer.StringSerializer;
import com.houbank.commons.serializer.hessionSerializer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {

    private static Serializer<Object> defaultSerializer = new hessionSerializer();

    private static Serializer<String> defaultKeySerializer = new StringSerializer();

    private static Serializer<String> stringValueSerializer = new StringSerializer();

    //服务器IP地址
    private static String host = "10.150.20.133";

    //端口
    private static int PORT = 6379;
    //密码
    //private static String AUTH = "123456";
    //连接实例的最大连接数
    private static int MAX_ACTIVE = 30;

    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = 20;

    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException
    private static int MAX_WAIT = 10000;

    //连接超时的时间
    private static int TIMEOUT = 10000;

    // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = true;

    private static JedisPool jedisPool = null;

    /**
     * 初始化Redis连接池
     */

    static {

        try {

            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAX_ACTIVE);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT);
            config.setTestOnBorrow(TEST_ON_BORROW);
            jedisPool = new JedisPool(config, host, PORT, TIMEOUT, null);

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    /**
     * 获取Jedis实例
     */

    public synchronized static Jedis getJedis() {

        Jedis resource = null;
        try {
            if (jedisPool != null) {
                resource = jedisPool.getResource();
                return resource;
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (resource != null)
                resource.close();
        }

    }

    public static String get(String key, Jedis jedis) {

        String result = null;
        try {
            byte[] bytes = jedis.get(defaultKeySerializer.serialize(key));
            result = defaultKeySerializer.deserialize(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) jedis.close();
        }
        return result;
    }

}

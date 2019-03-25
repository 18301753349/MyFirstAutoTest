package com.houbank.utils.redis;

import com.houbank.commons.serializer.Serializer;
import com.houbank.commons.serializer.StringSerializer;
import com.houbank.commons.serializer.hessionSerializer;
import lombok.extern.log4j.Log4j2;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public class JedisTemple {

  final String SUCEES = "OK";

  private JedisFactory jedisFactory;

  private Serializer<Object> defaultSerializer = new hessionSerializer();

  private Serializer<String> defaultKeySerializer = new StringSerializer();
  
  private Serializer<String> stringValueSerializer = new StringSerializer();


  public Map<String, Object> hgetAll(String key) {

    Map<String, Object> result = new HashMap<>();
    Jedis jedis = null;
    try {
      jedis = jedisFactory.getSource();
      if (null != jedis) {
        Map<byte[], byte[]> byteMap = jedis.hgetAll(defaultKeySerializer.serialize(key));
        for (byte[] mapKey : byteMap.keySet()) {
          result.put(defaultKeySerializer.deserialize(mapKey), defaultSerializer.deserialize(byteMap.get(mapKey)));
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (jedis != null) jedis.close();
    }
    return result;
  }
  
  public Map<String, String> hgetAllForString(String key) {

    Map<String, String> result = new HashMap<>();
    Jedis jedis = null;
    try {
      jedis = jedisFactory.getSource();
      if (null != jedis) {
        Map<byte[], byte[]> byteMap = jedis.hgetAll(defaultKeySerializer.serialize(key));
        for (byte[] mapKey : byteMap.keySet()) {
          result.put(defaultKeySerializer.deserialize(mapKey), stringValueSerializer.deserialize(byteMap.get(mapKey)));
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (jedis != null) jedis.close();
    }
    return result;
  }
  

  public boolean hmset(String key, Map<String, Object> hashMap, Integer expiredTime) {
    boolean result = false;
    Jedis jedis = null;
    if (hashMap == null || (hashMap.entrySet().size() == 0)) return false;
    try {
      jedis = jedisFactory.getSource();
      Map<byte[], byte[]> transferMap = new HashMap<>();
      for (String hashKey : hashMap.keySet())
        transferMap.put(defaultKeySerializer.serialize(hashKey), defaultSerializer.serialize(hashMap.get(hashKey)));
      String resultCode = jedis.hmset(defaultKeySerializer.serialize(key), transferMap);
      if (expiredTime != null && SUCEES.equals(resultCode)) jedis.expire(defaultKeySerializer.serialize(key), expiredTime);
      if (SUCEES.equals(resultCode)) {
        result = true;
      }

    } catch (Exception e) {
      log.error("redis hmset is{}" + key, e);
    } finally {
      if (jedis != null) {
        jedis.close();
      }
    }

    return result;
  }

  public String get(String key) {

    String result = null;
    Jedis jedis = null;
    try {
      jedis = jedisFactory.getSource();
      if (null != jedis) {
        byte[] bytes = jedis.get(defaultKeySerializer.serialize(key));
        result = defaultKeySerializer.deserialize(bytes);
      }

    } catch (Exception e) {
      log.error("redis get is{}" + key, e);
    } finally {
      if (jedis != null) jedis.close();
    }
    return result;
  }

  public Long del(String key) {
    Long result = null;
    Jedis jedis = null;
    try {
      jedis = jedisFactory.getSource();
      if (null != jedis) {
        result = jedis.del(defaultKeySerializer.serialize(key));
      }

    } catch (Exception e) {
      log.error("redis del is{}" + key, e);
    } finally {
      if (jedis != null) jedis.close();
    }
    return result;
  }

  public String setex(String key, int expire, String value) {
    String result = null;
    Jedis jedis = null;
    try {
      jedis = jedisFactory.getSource();
      if (null != jedis) {
        result = jedis.setex(key, expire, value);
      }

    } catch (Exception e) {
      log.error("redis setex is{}" + key, e);
    } finally {
      if (jedis != null) jedis.close();
    }
    return result;
  }

  public Long setIncr(String key, int seconds) {

    Long result = null;
    Jedis jedis = null;

    try {

      jedis = jedisFactory.getSource();

      if (null != jedis) {
        result = jedis.incr(key);
        jedis.expire(key, seconds);
      }

    } catch (Exception e) {
      log.error("redis setIncr is{}" + key, e);
    } finally {
      if (jedis != null) jedis.close();
    }

    return result;
  }

  public JedisFactory getJedisFactory() {
    return jedisFactory;
  }

  public void setJedisFactory(JedisFactory jedisFactory) {
    this.jedisFactory = jedisFactory;
  }

  public String getSet(String key, String value) {
    String result = null;
    Jedis jedis = null;
    try {
      jedis = jedisFactory.getSource();
      if (null != jedis) {
        result = jedis.getSet(key, value);
      }

    } catch (Exception e) {
      log.error("redis getSet is{}" + key, e);
    } finally {
      if (jedis != null) jedis.close();
    }
    return result;
  }

  public Long setnx(String key, String value) {
    Long result = null;
    Jedis jedis = null;
    try {
      jedis = jedisFactory.getSource();
      if (null != jedis) {
        result = jedis.setnx(key, value);
      }

    } catch (Exception e) {
      log.error("redis setnx is{}" + key, e);
    } finally {
      if (jedis != null) jedis.close();
    }
    return result;
  }

  public Long setnx(String key, String value,int timeout) {
    Long result = null;
    Jedis jedis = null;
    try {
      jedis = jedisFactory.getSource();
      if (null != jedis) {
        result = jedis.setnx(key, value);
        if(result > 0 && timeout > 0) {
          jedis.expire(key,timeout);
        }
      }

    } catch (Exception e) {
      log.error("redis [setnx] error,setnx is{}" + key, e);
    } finally {
      if (jedis != null) jedis.close();
    }
    return result;
  }

  public String hGet(String key, String field) {
    String result = null;
    Jedis jedis = null;
    try {
      jedis = jedisFactory.getSource();
      byte[] bytes = jedis.hget(defaultKeySerializer.serialize(key), defaultKeySerializer.serialize(field));
      result = defaultKeySerializer.deserialize(bytes);
    }catch (Exception e){
      log.error("redis hGet error, key:{}" + key + ",field," + field, e);
    }finally {
      if (jedis != null) jedis.close();
    }
    return result;

  }

  public long hSet(String key, String field, String value,int seconds) {
    long result = 0;
    Jedis jedis = null;
    try {
      jedis = jedisFactory.getSource();
      result = jedis.hset(key, field, value);
      jedis.expire(key, seconds);
    }catch (Exception e){
      log.error("redis hGet error, key:{}" + key + ",field," + field, e);
    }finally {
      if (jedis != null) jedis.close();
    }
    return result;
  }
}

package com.houbank.utils.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.util.Pool;

public class JedisFactory {

	public static final String PARRAM_ERROR="参数检查失败";

	public static final String INIT_ERROR="pool初始化失败";

	private int timeOut=30000;
	
	private Object lock = new Object();
	
    private JedisPoolConfig jedisPoolConfig;
	
	private String host;
	
	private Integer port;
	
	private String password;
	
	private Pool<Jedis> jedisPool;
	
	private volatile boolean shutdown =false;
	
	
	public void init(){
		
		jedisPool = new JedisPool(jedisPoolConfig, host, port, timeOut, password);
		Jedis jedis = jedisPool.getResource();
		if(null == jedis || !("PONG".equals(jedis.ping()))){
			throw new RuntimeException(INIT_ERROR);
		}
		shutdown =false;
		
	}
	
	public Jedis  getSource(){
		Jedis jedis =null;
		int count =0;
		do{	
		    if(shutdown){
		      break;
		    }
			if(jedisPool !=null){
				jedis = jedisPool.getResource();
				if(jedis !=null) break;
			}
			if((jedisPool ==null || jedis==null) && !shutdown){
				synchronized (lock) {
					if(jedisPool == null){
						init();
					}
				}
			}		
			count ++;
		}while(count <3);
		
		return jedis;
	}
	
	public void destory(){
		synchronized (lock) {
			if(jedisPool !=null){
				jedisPool.close();
				shutdown=true;
			}
		}
	}

	public JedisPoolConfig getJedisPoolConfig() {
		return jedisPoolConfig;
	}

	public void setJedisPoolConfig(JedisPoolConfig jedisPoolConfig) {
		this.jedisPoolConfig = jedisPoolConfig;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}

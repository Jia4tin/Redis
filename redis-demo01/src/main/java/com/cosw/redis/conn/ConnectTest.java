package com.cosw.redis.conn;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class ConnectTest {
	public static void main(String[] args) {
		//连接redis server，此时server必须处于开启状态
		Jedis jedis=new Jedis("127.0.0.1");
		System.out.println("连接成功:"+jedis);
		
		//storeList(jedis);
		
		getKeysFromJedisDB(jedis);
		
	}
	
	/**
	 * 操作字符串
	 * @param jedis
	 */
	public void storeStrig(Jedis jedis){
		jedis.set("test1","1111");
		System.out.println("存入数据："+jedis.get("test1"));
	}
	
	/**
	 * 操作List
	 * @param jedis
	 */
	public static void storeList(Jedis jedis){
		//存入list数据
		jedis.lpush("site-list", "11111");
		jedis.lpush("site-list", "22222");
		jedis.lpush("site-list", "33333");
		
		//读取数据
		List<String> lists=jedis.lrange("site-list", 0, 2);
		for (Iterator<String> iterator = lists.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			System.out.println(string);
		}
	}
	
	/**
	 * 查询DB中所有符合条件的key
	 * @param jedis
	 */
	public static void getKeysFromJedisDB(Jedis jedis){
		Set<String> keys=jedis.keys("*");
		for(Iterator<String> iterator=keys.iterator();iterator.hasNext();){
			String str=(String) iterator.next();
			System.out.println("key=="+str);
		}
	}
}

package com.cosw.redis.spring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

/**
 * 本代码需配合Redis 3.0及以上版本实现cluster功能</br>
 * 
 * 
 * @Description:
 * @Company: Shanghai COS Software
 * @Copyright: Copyright (c)2017
 * @author taoww
 * @version 1.0
 * @Create:  2017年12月28日 下午3:50:01 
 * @Modification History
 * @Date Author Version Description
 */
public class SpringJedisClusterTest {
	
	private static ApplicationContext context = null;
	private static RedisTemplate<String, User> redisTemplate = null;

	static {
		context = new ClassPathXmlApplicationContext(
				"classpath*:applicationContext-cluster.xml");
		redisTemplate = (RedisTemplate) context.getBean("redisTemplate");
	}
	
	public static void main(String[] args) {
		User user = new User();
		user.setUid("11112222322233");
		user.setAddress("上海市虹漕路A座4楼");
		user.setMobile("mobile");
		user.setPostCode("postcode");
		
		save(user);
		
		System.out.println(read(user));
	}
	
	
	public static void save(User user) {
		redisTemplate.opsForValue().set(user.getUid(), user);
	}

	public static User read(User user) {
		return redisTemplate.opsForValue().get(user.getUid());
	}

	public static void listSave(User user) {
		List<User> users = new ArrayList<User>();
		users.add(user);
		redisTemplate.opsForList().leftPushAll(
				"user.list.ops." + user.getUid(), users);
	}

	public static void listRead(User user) {
		List<User> users = redisTemplate.opsForList().range(
				"user.list.ops." + user.getUid(), 0, -1);
		for (Iterator iterator = users.iterator(); iterator.hasNext();) {
			User user1 = (User) iterator.next();
			System.out.println(user1);
		}
	}

	public static void hashSave(User user) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("address", user.getAddress());
		map.put("mobile", user.getMobile());
		map.put("postCode", user.getPostCode());
		redisTemplate.opsForHash()
				.putAll("user.hash.ops." + user.getUid(), map);
	}

	public static void hashRead(User user) {
		Map<Object, Object> map = redisTemplate.opsForHash().entries(
				"user.hash.ops." + user.getUid());
		Iterator<Entry<Object, Object>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Object, Object> type = it.next();
			System.out.println("key:" + type.getKey() + ",value:"
					+ type.getValue());
		}
	}

	public static void setSave(User user) {

		/*
		 * 竟然没有批量新增方法？？？？ 
		 */
		redisTemplate.opsForSet().add("user.set.ops." + user.getUid(), user);
	}

	public static void setRead(User user) {
		Set<User> sets = redisTemplate.opsForSet().members(
				"user.set.ops." + user.getUid());
		for (Iterator iterator = sets.iterator(); iterator.hasNext();) {
			User user1 = (User) iterator.next();
			System.out.println("user1:" + user1);
		}
	}

	public static void zsetSave(User user) {
        Set<TypedTuple<User>> zsets=new HashSet<TypedTuple<User>>();
        TypedTuple<User> tuple0=new DefaultTypedTuple(user, 1d);
        zsets.add(tuple0);
		redisTemplate.opsForZSet().add("user.zset.ops." + user.getUid(), zsets);
	}
	
	public static void zsetRead(User user) {
        Set<TypedTuple<User>> zsets=new HashSet<TypedTuple<User>>();
        TypedTuple<User> tuple0=new DefaultTypedTuple(user, 1d);
        zsets.add(tuple0);
		Set<User> users=redisTemplate.opsForZSet().range("user.zset.ops." + user.getUid(), 0, -1);
		for (Iterator iterator = users.iterator(); iterator.hasNext();) {
			User user2 = (User) iterator.next();
			System.out.println("user2:"+user2);
		}
	}

}

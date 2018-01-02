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
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

/**
 * 
 * RedisTemplate高级操作 ValueOperations<K, V> valueOps; ListOperations<K, V>
 * listOps; SetOperations<K, V> setOps; ZSetOperations<K, V> zSetOps;
 * 案例参考1：https://www.cnblogs.com/edwinchen/p/3816938.html
 * 案例参考2：http://www.cnblogs.com/luochengqiuse/p/4641256.html
 * 
 * 目前redis并未实现集群技术，redis3.0之后版本可能出现集群技术,拭目以待
 * 
 * spring-data-redis已经对jedis、jredis进行了封装，
 * （1）完美兼容redis sentinel部署模式、
 *    --sentinel采用投票方式进行故障切换，而投票方式下原则是少数服从多数，所以sentinel一般至少是3台
 * （2）兼容redis-cluster部署模式
 * （3）单节点redis部署模式
 * 提供统一的API(RedisTemplate)来调用不同的部署模式，完全不用担心redis部署模式变化导致redis客户端代码做调整
 * 
 * @Description:
 * @Company: Shanghai COS Software
 * @Copyright: Copyright (c)2017
 * @author taoww
 * @version 1.0
 * @Create: 2017年12月27日 下午3:38:20
 * @Modification History
 * @Date Author Version Description
 */
public class SpringJedisRedisOperationsTest {

	private static ApplicationContext context = null;
	private static RedisTemplate<String, User> redisTemplate = null;

	static {
		context = new ClassPathXmlApplicationContext(
				"classpath*:applicationContext.xml");
		redisTemplate = (RedisTemplate) context.getBean("redisTemplate");
	}

	public static void main(String[] args) {
		User user = new User();
		user.setUid("1111112333");
		user.setAddress("上海市虹漕路A座4楼");
		user.setMobile("mobile");
		user.setPostCode("postcode");

		/*
		 * save(user);
		 * 
		 * user=read(user); System.out.println("user==="+user);
		 */

		/*
		 * listSave(user);
		 * 
		 * listRead(user);
		 */

		/*
		 * hashSave(user); hashRead(user);
		 */

		/*setSave(user);

		setRead(user);*/
		
		zsetSave(user);
		
		zsetRead(user);
		
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

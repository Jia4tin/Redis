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
 * 目前redis并未实现集群技术，redis3.0之后版本可能出现集群技术,拭目以待
 * 
 * spring-data-redis已经对jedis、jredis进行了封装，
 * （1）完美兼容redis sentinel部署模式、
 *    --sentinel采用投票方式进行故障切换，而投票方式下原则是少数服从多数，所以sentinel一般至少是3台
 *    --参考：https://www.cnblogs.com/aliyunblogs/p/5728861.html
 *    --Redis-Sentinel作为官方推荐的HA解决方案：https://segmentfault.com/a/1190000002690506
 *    --redis server sentinel配置：http://blog.csdn.net/liuchuanhong1/article/details/53206028
 *      --在测试sentinel集群之前，需要在redis服务端进行sentinel模式配置
 * （2）兼容redis-cluster部署模式
 * （3）单节点redis部署模式，
 * 提供统一的API(RedisTemplate)来调用不同的部署模式，完全不用担心redis部署模式变化导致redis客户端代码做调整
 * 
 * 参考案例：http://aperise.iteye.com/blog/2342615
 * 
 * sentinel配置顺序：
 * 1、配置redis server sentinel，参考：http://blog.csdn.net/liuchuanhong1/article/details/53206028
 * 2、配置java项目：http://aperise.iteye.com/blog/2342615
 * 此种方式下搭建的sentinel集群（主从方式），能够实现故障切换；故障节点修复重启后，也可以自动融入集群，作为master的从数据库
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
public class SpringJedisSentinelTest {
	
	private static ApplicationContext context = null;
	private static RedisTemplate<String, User> redisTemplate = null;

	static {
		context = new ClassPathXmlApplicationContext(
				"classpath*:applicationContext-sentinel.xml");
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

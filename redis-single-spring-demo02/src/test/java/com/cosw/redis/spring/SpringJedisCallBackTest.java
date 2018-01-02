package com.cosw.redis.spring;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

/**
 * 
 * 案例参考:http://snowolf.iteye.com/blog/1667104
 * 
 * 基本操作，存储String类型数据
 * 
 * 目前redis并未实现集群技术，redis3.0之后版本可能出现集群技术,拭目以待
 * 
 * spring-data-redis已经对jedis、jredis进行了封装，
 * （1）完美兼容redis sentinel部署模式、
 *    --sentinel采用投票方式进行故障切换，而投票方式下原则是少数服从多数，所以sentinel一般至少是3台
 * （2）兼容redis-cluster部署模式
 * （3）单节点redis部署模式，
 * 提供统一的API(RedisTemplate)来调用不同的部署模式，完全不用担心redis部署模式变化导致redis客户端代码做调整
 * 
 * @Description:
 * @Company: Shanghai COS Software
 * @Copyright: Copyright (c)2017
 * @author taoww
 * @version 1.0
 * @Create: 2017年12月27日 下午12:35:13
 * @Modification History
 * @Date Author Version Description
 */
public class SpringJedisCallBackTest {
	private static ApplicationContext context = null;
	private static RedisTemplate<Serializable, Serializable> redisTemplate = null;

	static {
		context = new ClassPathXmlApplicationContext(
				"classpath*:applicationContext.xml");
		redisTemplate = (RedisTemplate) context.getBean("redisTemplate");
	}

	public static void main(String[] args) {
		User user = new User();
		user.setUid("1111112255");
		user.setAddress("上海市虹漕路A座4楼");
		user.setMobile("");
		user.setPostCode("");

		/*
		 * save(user);
		 * 
		 * user= read(user.getUid()); System.out.println("save==="+user);
		 * 
		 * user.setAddress("hhhhhhhhhhhh");
		 * 
		 * save(user);
		 * 
		 * delete(user.getUid());
		 * 
		 * user= read(user.getUid()); System.out.println("update==="+user);
		 */

		/*
		 * hashSave(user);
		 * 
		 * user=hashRead(user.getUid()); System.out.println(user);
		 */

	/*	listSave(user);

		
		listRead(user);*/
		
		
		/*setSave(user);
		
		setRead(user);*/
		
		zsetSave(user);
		
		zsetRead(user);
		
		
	}

	/**
	 * 新增/修改 key不存在则新增，key存在则修改对应value值 基本String操作：set
	 * 
	 * @param user
	 */
	public static void save(final User user) {
		redisTemplate.execute(new RedisCallback<User>() {

			public User doInRedis(RedisConnection connection)
					throws DataAccessException {
				connection.set(
						redisTemplate.getStringSerializer().serialize(
								"user.uid." + user.getUid()),
						redisTemplate.getStringSerializer().serialize(
								user.getAddress()));
				return null;
			}
		});

	}

	/**
	 * 基本String操作：get
	 * 
	 * @param uid
	 * @return
	 */
	public static User read(final String uid) {
		return redisTemplate.execute(new RedisCallback<User>() {
			public User doInRedis(RedisConnection connection)
					throws DataAccessException {
				byte[] key = redisTemplate.getStringSerializer().serialize(
						"user.uid." + uid);
				if (connection.exists(key)) {
					byte[] value = connection.get(key);
					String address = redisTemplate.getStringSerializer()
							.deserialize(value);
					User user = new User();
					user.setAddress(address);
					user.setUid(uid);
					return user;
				}
				return null;
			}
		});
	}

	/**
	 * 基本String操作：del
	 * 
	 * @param uid
	 */
	public static void delete(final String uid) {
		redisTemplate.execute(new RedisCallback<Object>() {

			public Object doInRedis(RedisConnection connection)
					throws DataAccessException {
				byte[] key = redisTemplate.getStringSerializer().serialize(
						"user.uid." + uid);
				long count = connection.del(key);
				System.out.println("count====" + count);
				return null;
			}
		});
	}

	/**
	 * 保存hash表数据
	 * 
	 * @param user
	 */
	public static void hashSave(final User user) {
		redisTemplate.execute(new RedisCallback<User>() {

			public User doInRedis(RedisConnection connection)
					throws DataAccessException {
				byte[] key = redisTemplate.getStringSerializer().serialize(
						"user.info.uid." + user.getUid());

				// 绑定hash key
				BoundHashOperations<Serializable, byte[], byte[]> operations = redisTemplate
						.boundHashOps(key);
				operations.put(
						redisTemplate.getStringSerializer()
								.serialize("address"),
						redisTemplate.getStringSerializer().serialize(
								user.getAddress()));
				operations
						.put(redisTemplate.getStringSerializer().serialize(
								"mobile"), redisTemplate.getStringSerializer()
								.serialize(user.getMobile()));
				operations.put(
						redisTemplate.getStringSerializer().serialize(
								"postCode"),
						redisTemplate.getStringSerializer().serialize(
								user.getPostCode()));

				connection.hMSet(key, operations.entries());
				return null;
			}
		});

	}

	/**
	 * hash表数据读取
	 * 
	 * @param uid
	 * @return
	 */
	public static User hashRead(final String uid) {
		return redisTemplate.execute(new RedisCallback<User>() {

			public User doInRedis(RedisConnection connection)
					throws DataAccessException {
				byte[] key = redisTemplate.getStringSerializer().serialize(
						"user.info.uid." + uid);
				if (connection.exists(key)) {
					List<byte[]> value = connection.hMGet(
							key,
							redisTemplate.getStringSerializer().serialize(
									"address"),
							redisTemplate.getStringSerializer().serialize(
									"mobile"), redisTemplate
									.getStringSerializer()
									.serialize("postCode"));
					User user = new User();
					user.setAddress(redisTemplate.getStringSerializer()
							.deserialize(value.get(0)));
					user.setMobile(redisTemplate.getStringSerializer()
							.deserialize(value.get(1)));
					user.setPostCode(redisTemplate.getStringSerializer()
							.deserialize(value.get(2)));
					user.setUid(uid);
					return user;
				}
				return null;
			}
		});
	}

	/**
	 * 保存list
	 * @param user
	 */
	public static void listSave(final User user) {

		byte[] key = redisTemplate.getStringSerializer().serialize(
				"user.list." + user.getUid());
		// 绑定hash key
		BoundListOperations<Serializable, Serializable> operations = redisTemplate
				.boundListOps(key);
		operations.rightPush("11111");
		operations.rightPush("22222");
		operations.rightPush("33333");
		operations.rightPush("44444");

	}

	/**
	 * 读取list
	 * @param user
	 */
	public static void listRead(final User user) {

		byte[] key = redisTemplate.getStringSerializer().serialize(
				"user.list." + user.getUid());
		// 绑定hash key
		BoundListOperations<Serializable, Serializable> operations = redisTemplate
				.boundListOps(key);
		List<Serializable> list=operations.range(0, -1);
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			Serializable serializable = (Serializable) iterator.next();
			System.out.println(serializable);
		}
	}
	
	/**
	 * 保存set
	 * @param user
	 */
	public static void setSave(final User user) {

		byte[] key = redisTemplate.getStringSerializer().serialize(
				"user.set." + user.getUid());
		// 绑定hash key
		BoundSetOperations<Serializable, Serializable> operations = redisTemplate.boundSetOps(key);
		operations.add("set11111");
		operations.add("set33333");
		operations.add("set22222");
	}
	
	
	/**
	 * 读取list
	 * @param user
	 */
	public static void setRead(final User user) {
		byte[] key = redisTemplate.getStringSerializer().serialize(
				"user.set." + user.getUid());
		// 绑定hash key
		BoundSetOperations<Serializable, Serializable> operations = redisTemplate.boundSetOps(key);
		Set<Serializable> set=operations.members();
		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			Serializable serializable = (Serializable) iterator.next();
			System.out.println(serializable);
		}
	}
	
	public static void zsetSave(final User user) {

		byte[] key = redisTemplate.getStringSerializer().serialize(
				"user.zset." + user.getUid());
		// 绑定hash key
		BoundZSetOperations<Serializable, Serializable> operations = redisTemplate.boundZSetOps(key);
		operations.add("zset1111", 7.0);
		operations.add("zset2222",2.0);
		operations.add("zset3333",3.0);
	}
	
	
	/**
	 * 读取list
	 * @param user
	 */
	public static void zsetRead(final User user) {
		byte[] key = redisTemplate.getStringSerializer().serialize(
				"user.zset." + user.getUid());
		// 绑定hash key
		BoundZSetOperations<Serializable, Serializable> operations = redisTemplate.boundZSetOps(key);
		Set<TypedTuple<Serializable>> set=operations.rangeWithScores(0,-1);
		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			TypedTuple serializable = (TypedTuple) iterator.next();
			System.out.println(serializable.getValue()+","+serializable.getScore());
		}
	}
}

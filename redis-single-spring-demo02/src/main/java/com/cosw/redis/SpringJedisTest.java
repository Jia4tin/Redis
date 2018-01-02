package com.cosw.redis;

import java.io.Serializable;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

public class SpringJedisTest {
	private static ApplicationContext context=new ClassPathXmlApplicationContext("classpath*:applicationContext.xml");
	
	public static void main(String[] args) {
        RedisTemplate<Serializable, Serializable> redisTemplate=(RedisTemplate<Serializable, Serializable>) context.getBean("redisTemplate");
        System.out.println(redisTemplate);
	}

}

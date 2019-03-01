package vn.yotel.yomi.config;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableTransactionManagement
@EnableCaching
@Profile({ "REDIS_yomi" })
public class RedisCacheManagerConfig extends CachingConfigurerSupport {

	 @Value("${redis.host}")
	 private String redisHost;
	 
	 @Value("${redis.port}") 
	 private  int redisPort;
	 
	 @Value("${redis.pool.max-idle}") 
	 private  int redisPoolMaxIdle;
	 
	 @Value("${redis.pool.min-idle}") 
	 private  int redisPoolMinIdle;
	 
	 @Value("${redis.pool.max-active}") 
	 private  int redisPoolMaxActive;
	 
	 @Value("${redis.default.expiration}") 
	 private  int redisDefaultExpiration;
	 
	 @Value("${redis.database}")
	 private  int redisDatabase;
	 
	 @Bean
	 JedisConnectionFactory jedisConnectionFactory() {
	     JedisConnectionFactory factory = new JedisConnectionFactory();
	     factory.setHostName(redisHost);
	     factory.setPort(redisPort);
	     factory.setUsePool(true);
	     factory.setTimeout(2000);
	     factory.setDatabase(redisDatabase);
	     JedisPoolConfig poolConfig = new JedisPoolConfig();
	     poolConfig.setMinIdle(redisPoolMinIdle);
	     poolConfig.setMaxIdle(redisPoolMaxIdle);
	     poolConfig.setMaxTotal(redisPoolMaxActive);
	     poolConfig.setTestOnBorrow(true);
	     
	     factory.setPoolConfig(poolConfig);
	     return factory;
	 }

	 /**
	  * redis template definition
	  * @return
	  */
	 @Bean
	 RedisTemplate<String, Object> redisTemplate() {
	     RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
	     redisTemplate.setConnectionFactory(jedisConnectionFactory());
	     // key as string 
	     StringRedisSerializer stringSerializer = new StringRedisSerializer();
	     redisTemplate.setKeySerializer(stringSerializer);
	     redisTemplate.setHashKeySerializer(stringSerializer);
	     
	     return redisTemplate;
	 }

	 @Override
 	 public CacheManager cacheManager() {
		 RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate());
		 // Number of seconds before expiration. Defaults to unlimited (0)
		 cacheManager.setDefaultExpiration(redisDefaultExpiration);
		 cacheManager.setUsePrefix(true);
	     return cacheManager;
		}
	 
	 @Override
	 @Bean
	 public KeyGenerator keyGenerator() {
	    return new KeyGenerator() {
			@Override
			public Object generate(Object target, Method method, Object... params) {
		        StringBuilder sb = new StringBuilder();
		        sb.append(method.getName());
		        sb.append(":params:");
		        for (Object obj : params) {
		        	sb.append(String.format("[%s]", obj));
		        }
		        return sb.toString();
			}
		};
	  }
}

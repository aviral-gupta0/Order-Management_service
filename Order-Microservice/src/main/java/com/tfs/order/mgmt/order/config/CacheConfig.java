/*
 * package com.tfs.order.mgmt.order.config;
 * 
 * import org.springframework.beans.factory.annotation.Value; import
 * org.springframework.cache.annotation.EnableCaching; import
 * org.springframework.context.annotation.Bean; import
 * org.springframework.context.annotation.Configuration; import
 * org.springframework.data.redis.cache.RedisCacheManager; import
 * org.springframework.data.redis.connection.RedisConnectionFactory; import
 * org.springframework.data.redis.connection.RedisStandaloneConfiguration;
 * import
 * org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
 * 
 * @EnableCaching
 * 
 * @Configuration public class CacheConfig {
 * 
 * @Value("${spring.redis.host}") private String redisHost;
 * 
 * @Value("${spring.redis.port}") private int redisPort;
 * 
 * @Bean public LettuceConnectionFactory redisConnectionFactory() {
 * RedisStandaloneConfiguration configuration = new
 * RedisStandaloneConfiguration(redisHost, redisPort);
 * 
 * return new LettuceConnectionFactory(configuration); }
 * 
 * @Bean public RedisCacheManager cacheManager(RedisConnectionFactory
 * redisConnectionFactory) { return
 * RedisCacheManager.create(redisConnectionFactory); }
 * 
 * }
 */
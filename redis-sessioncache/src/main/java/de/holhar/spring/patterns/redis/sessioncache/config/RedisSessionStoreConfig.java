package de.holhar.spring.patterns.redis.sessioncache.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.holhar.spring.patterns.redis.sessioncache.listener.CustomSessionListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.session.SessionRepository;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.SessionEventHttpSessionListenerAdapter;

import javax.servlet.http.HttpSessionListener;
import java.util.Arrays;

@Configuration
@EnableCaching
@EnableRedisHttpSession
public class RedisSessionStoreConfig {

    private int redisSessionExpirationInSecs = 1800;

    /*
     * Disable automatic configuration by adding ConfigureRedisAction.NO_OP as a bean.
     */
    @Bean
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        var mapper = new ObjectMapper();
        mapper.setVisibility(mapper.getVisibilityChecker().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        mapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.NON_FINAL, "@class"); // enable default typing
        mapper.registerModules(SecurityJackson2Modules.getModules(this.getClass().getClassLoader()));
        return new GenericJackson2JsonRedisSerializer(mapper);
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration(RedisSerializer<Object> springSessionDefaultRedisSerializer) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(springSessionDefaultRedisSerializer));
    }

    @Bean
    @Qualifier("sessionCacheManager")
    public CacheManager sessionCacheManager(RedisConnectionFactory redisConnectionFactory,
                                            RedisCacheConfiguration cacheConfiguration) {
        return RedisCacheManager
                .builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration).build();
    }

    /*
     * Ensure HttpSessionListener receives SessionDestroyedEvents - START
     *
     * In order for our redisSessionStoreListener to work as expected, we have to configure RedisOperations,
     * SessionRepository, and SessionEventHttpSessionListenerAdapter manually, see POI for more infos
     *
     * POI: https://docs.spring.io/spring-session/docs/current/reference/html5/#httpsession-httpsessionlistener
     */
    @Bean
    public RedisOperations<Object, Object> customRedisOperations(RedisConnectionFactory redisConnectionFactory,
                                                               RedisSerializer<Object> springSessionDefaultRedisSerializer) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(springSessionDefaultRedisSerializer);
        return redisTemplate;
    }

    @Primary
    @Bean
    public SessionRepository customSessionRepository(RedisOperations<Object, Object> customRedisOperations,
                                                   ApplicationEventPublisher eventPublisher) {
        var sessionRepository = new RedisIndexedSessionRepository(customRedisOperations);
        sessionRepository.setApplicationEventPublisher(eventPublisher);
        sessionRepository.setDefaultMaxInactiveInterval(redisSessionExpirationInSecs);
        return sessionRepository;
    }

    @Bean
    public SessionEventHttpSessionListenerAdapter customSessionEventHttpSessionListenerAdapter(HttpSessionListener sessionStoreListener) {
        return new SessionEventHttpSessionListenerAdapter(Arrays.asList(sessionStoreListener));
    }

    @Bean
    public HttpSessionListener sessionStoreListener() {
        return new CustomSessionListener();
    }
    /*
     * Ensure HttpSessionListener receives SessionDestroyedEvents - END
     */
}

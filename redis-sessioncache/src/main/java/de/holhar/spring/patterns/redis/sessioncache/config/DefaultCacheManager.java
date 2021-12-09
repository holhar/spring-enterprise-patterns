package de.holhar.spring.patterns.redis.sessioncache.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * The 'cacheManager' gets initialized automatically, this custom bean serves just as a demo. E.g., you could extend
 * the cacheManager to include and manage multiple different caches.
 */
@Primary
@Component("cacheManager")
public class DefaultCacheManager implements CacheManager {

    @Autowired
    private CacheManager sessionCacheManager;

    @Override
    public Cache getCache(String name) {
        return sessionCacheManager.getCache(name);
    }

    @Override
    public Collection<String> getCacheNames() {
        return sessionCacheManager.getCacheNames();
    }
}

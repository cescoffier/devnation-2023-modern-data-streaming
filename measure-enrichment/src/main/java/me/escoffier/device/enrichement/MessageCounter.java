package me.escoffier.device.enrichement;

import io.quarkus.cache.CaffeineCache;
import io.quarkus.cache.runtime.caffeine.CaffeineCacheImpl;
import io.quarkus.cache.runtime.caffeine.CaffeineCacheInfo;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class MessageCounter {

    private final Map<String, CaffeineCache> caches = new ConcurrentHashMap<>();
    private Duration ttl = Duration.ofMinutes(1);


    // For testing purpose only
    public void setTtl(Duration ttl) {
        this.ttl = ttl;
    }

    public void inc(String name) {
        CaffeineCache cache = getCache(name);
        long key = System.currentTimeMillis();
        cache.put(key, CompletableFuture.completedFuture(key));
    }

    public long count(String name) {
        return getCache(name).keySet().size();
    }

    private CaffeineCache getCache(String name) {
        return caches.computeIfAbsent(name, k -> {
            var info = new CaffeineCacheInfo();
            info.name = name;
            info.expireAfterWrite = ttl;
            info.metricsEnabled = false;
            return new CaffeineCacheImpl(info, false);
        });
    }


}

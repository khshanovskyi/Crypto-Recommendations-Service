package epam.com.khshanovskyi.config;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This is Configuration class that contains methods and regulations for cache evictions.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class CacheEvictionConfig {

    @Value("${cache.name.for.parsed.dto.name.for.eviction}")
    private String cacheNameForParsedDto;
    private final CacheManager cacheManager;

    @Scheduled(cron ="${cache.eviction.cron.once.per.day}")
    public void evictCachesForParsedDtoFromFiles() {
        log.debug("Start eviction caches for parsed files into DTOs...");
        cacheManager.getCacheNames()
          .stream()
          .filter(cacheName -> cacheName.startsWith(cacheNameForParsedDto))
          .forEach(cacheName -> {
              Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
              log.trace("Eviction for cache with the name {}", cacheName);
          });

        log.debug("Caches for parsed files into DTOs are successfully evicted!");
    }

}

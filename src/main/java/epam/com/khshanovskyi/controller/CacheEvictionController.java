package epam.com.khshanovskyi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import epam.com.khshanovskyi.config.CacheEvictionConfig;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("cache/evict")
@RequiredArgsConstructor
@ApiIgnore
public class CacheEvictionController {

    private final CacheEvictionConfig cacheEvictionConfig;

    //TODO: add security
    @GetMapping(value = "/for-crypto-advice")
    public String evictCachesForAdvice() {
        cacheEvictionConfig.evictCachesForParsedDtoFromFiles();
        return "Cache for Crypto advice successfully evicted!";
    }
}

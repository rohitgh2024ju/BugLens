
package dev.rohit.buglens.Configpaths;

import java.io.IOException;
import java.time.Duration;
import java.nio.file.Files;
import java.nio.file.Path;

import org.ehcache.Cache; 
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnalysisCacheConfig {

    @Bean(destroyMethod = "close")
    public PersistentCacheManager ehcacheAnalysisCacheManager()
            throws IOException {

        Path cacheDir = BugLensPaths.ANALYSIS_CACHE_DIR;

        Files.createDirectories(cacheDir);

        return CacheManagerBuilder.newCacheManagerBuilder()
                .with(CacheManagerBuilder.persistence(
                        cacheDir.toFile()))
                .withCache(
                        "analysisCache",
                        CacheConfigurationBuilder
                                .newCacheConfigurationBuilder(
                                        String.class,
                                        String.class,
                                        ResourcePoolsBuilder
                                                .newResourcePoolsBuilder()
                                                .disk(1, MemoryUnit.GB, false))
                                .withExpiry(
                                        ExpiryPolicyBuilder
                                                .timeToLiveExpiration(
                                                        Duration.ofMinutes(5))))
                .build(true);
    }

    @Bean
    public Cache<String, String> analysisCache(
            PersistentCacheManager ehcacheAnalysisCacheManager) {

        return ehcacheAnalysisCacheManager.getCache(
                "analysisCache",
                String.class,
                String.class);
    }
}

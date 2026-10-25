package dev.rohit.buglens.Application;

import static org.junit.jupiter.api.Assertions.*;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

class AnalysisCacheServiceTest {

    private PersistentCacheManager cacheManager;
    private AnalysisCacheService cacheService;

    @BeforeEach
    void setUp() throws Exception {
        Path testCacheDir = Files.createTempDirectory("buglens-cache-test-");

        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .with(CacheManagerBuilder.persistence(testCacheDir.toFile()))
                .withCache(
                        "analysisCache",
                        CacheConfigurationBuilder
                                .newCacheConfigurationBuilder(
                                        String.class,
                                        String.class,
                                        ResourcePoolsBuilder.newResourcePoolsBuilder()
                                                .disk(10, MemoryUnit.MB, false))
                                .withExpiry(
                                        ExpiryPolicyBuilder.timeToLiveExpiration(
                                                Duration.ofMinutes(5))))
                .build(true);

        Cache<String, String> cache = cacheManager.getCache(
                "analysisCache", String.class, String.class);

        cacheService = new AnalysisCacheService(
                cache,
                new ObjectMapper().findAndRegisterModules());
    }

    @AfterEach
    void tearDown() {
        if (cacheManager != null) {
            cacheManager.close();
        }
    }

    @Test
    void shouldReturnNullForCacheMiss() {
        assertNull(cacheService.getCacheResponse("missing-key", 0, 0, false));
    }

    @Test
    void shouldGenerateDeterministicCacheKey() {
        String first = cacheService.generateCacheKey(
                "client-001");

        String second = cacheService.generateCacheKey(
                "client-001");

        assertEquals(first, second);
    }

    @Test
    void shouldGenerateDifferentKeysForDifferentClients() {
        String first = cacheService.generateCacheKey(
                "client-001");

        String second = cacheService.generateCacheKey(
                "client-002");

        assertNotEquals(first, second);
    }

    @Test
    void shouldGenerateDifferentKeysForDifferentSettings() {
        String first = cacheService.generateCacheKey(
                "client-001");

        String second = cacheService.generateCacheKey(
                "client-001");

        assertNotEquals(first, second);
    }

    @Test
    void shouldInvalidateCacheEntry() {
        String key = "test-entry";

        cacheService.invalidate(key);

        assertNull(cacheService.getCacheResponse(key, 0, 0, false));
    }
}
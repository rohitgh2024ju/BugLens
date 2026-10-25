
package dev.rohit.buglens.ConfigPaths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.rohit.buglens.Configpaths.AnalysisCacheConfig;
import dev.rohit.buglens.Configpaths.BugLensPaths;

class AnalysisCacheConfigTest {

    private PersistentCacheManager cacheManager;
    private Cache<String, String> analysisCache;

    @BeforeEach
    void setUp() throws IOException {
        AnalysisCacheConfig config = new AnalysisCacheConfig();

        cacheManager = config.ehcacheAnalysisCacheManager();
        analysisCache = config.analysisCache(cacheManager);
    }

    @AfterEach
    void tearDown() {
        if (cacheManager != null) {
            cacheManager.close();
        }
    }

    @Test
    void shouldStoreAndRetrieveAnalysisResult() {
        String key = "test-analysis-001";
        String value = "{\"incidentCount\":3}";

        analysisCache.put(key, value);

        assertEquals(value, analysisCache.get(key));
    }

    @Test
    void shouldReturnNullForMissingKey() {
        assertNull(analysisCache.get("missing-analysis"));
    }

    @Test
    void shouldRemoveCachedAnalysis() {
        String key = "test-analysis-002";

        analysisCache.put(key, "{\"incidentCount\":5}");
        analysisCache.remove(key);

        assertNull(analysisCache.get(key));
    }

    @Test
    void shouldAcceptLargeAnalysisResult() {
        String key = "test-large-analysis";
        String value = "x".repeat(100_000);

        analysisCache.put(key, value);

        String result = analysisCache.get(key);

        assertNotNull(result);
        assertEquals(value, result);
    }

    @Test
    void shouldExpireCachedEntryAfterTtl() throws Exception {
        var testCacheManager = org.ehcache.config.builders.CacheManagerBuilder
                .newCacheManagerBuilder()
                .with(
                        org.ehcache.config.builders.CacheManagerBuilder
                                .persistence(
                                        BugLensPaths.ANALYSIS_CACHE_DIR
                                                .resolve("ttl-test")
                                                .toFile()))
                .withCache(
                        "ttlTestCache",
                        org.ehcache.config.builders.CacheConfigurationBuilder
                                .newCacheConfigurationBuilder(
                                        String.class,
                                        String.class,
                                        org.ehcache.config.builders.ResourcePoolsBuilder
                                                .newResourcePoolsBuilder()
                                                .disk(
                                                        10,
                                                        org.ehcache.config.units.MemoryUnit.MB,
                                                        false))
                                .withExpiry(
                                        org.ehcache.config.builders.ExpiryPolicyBuilder
                                                .timeToLiveExpiration(
                                                        java.time.Duration
                                                                .ofSeconds(1))))
                .build(true);

        try {
            var cache = testCacheManager.getCache(
                    "ttlTestCache", String.class, String.class);

            cache.put("ttl-key", "temporary-result");

            assertEquals("temporary-result", cache.get("ttl-key"));

            Thread.sleep(1500);

            assertNull(cache.get("ttl-key"));
        } finally {
            testCacheManager.close();
        }
    }
}

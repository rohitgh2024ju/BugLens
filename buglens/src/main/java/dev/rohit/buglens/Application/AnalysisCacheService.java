
package dev.rohit.buglens.Application;

import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AnalysisCacheService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisCacheService.class);

    private static final String PIPELINE_VERSION = "v1";

    private final Cache<String, String> analysisCache;
    private final ObjectMapper objectMapper;

    public AnalysisCacheService(
            Cache<String, String> analysisCache,
            ObjectMapper objectMapper) {

        this.analysisCache = analysisCache;
        this.objectMapper = objectMapper;
    }

    public String generateCacheKey(String clientId) {
        return "analysis:" + PIPELINE_VERSION + ":" + clientId;
    }

    public AnalysisResponse getCacheResponse(
            String clientId,
            long seconds,
            double threshold,
            boolean print) {

        String cacheKey = generateCacheKey(clientId);

        try {
            String json = analysisCache.get(cacheKey);

            if (json == null) {
                return null;
            }

            CachedAnalysisEntry entry = objectMapper.readValue(json, CachedAnalysisEntry.class);

            boolean settingsMatch = seconds == entry.getSeconds()
                    && Double.compare(threshold, entry.getThreshold()) == 0
                    && print == entry.isPrint();

            if (!settingsMatch || entry.getResponse() == null) {
                analysisCache.remove(cacheKey);
                return null;
            }

            return entry.getResponse();

        } catch (Exception e) {
            log.warn(
                    "Unable to read cached analysis; treating as a cache miss.",
                    e);

            try {
                analysisCache.remove(cacheKey);
            } catch (RuntimeException removeException) {
                log.warn(
                        "Unable to remove invalid cache entry.",
                        removeException);
            }

            return null;
        }
    }

    public void cacheResponse(
            String clientId,
            long seconds,
            double threshold,
            boolean print,
            AnalysisResponse response) {

        if (response == null) {
            log.warn("Cannot cache a null analysis response.");
            return;
        }

        String cacheKey = generateCacheKey(clientId);

        CachedAnalysisEntry entry = new CachedAnalysisEntry(
                seconds,
                threshold,
                print,
                response);

        try {
            String json = objectMapper.writeValueAsString(entry);
            analysisCache.put(cacheKey, json);

        } catch (JsonProcessingException | RuntimeException e) {
            log.warn(
                    "Unable to cache analysis response; continuing without caching.",
                    e);
        }
    }

    public void invalidate(String clientId) {
        try {
            analysisCache.remove(generateCacheKey(clientId));

        } catch (RuntimeException e) {
            log.warn("Unable to invalidate cached analysis.", e);
        }
    }
}

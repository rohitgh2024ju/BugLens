package dev.rohit.buglens.QueryLayer.config;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.mvstore.MVStoreModule;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.dizitart.no2.common.mapper.JacksonMapperModule;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NitriteMultiTenantConfig {

    private static final Map<String, Nitrite> dbRegistry = new ConcurrentHashMap<>();
    private static final String BASE_DB_DIR = "buglens/Database/tenants/";

    private NitriteMultiTenantConfig() {
    }

    // Removed 'synchronized' to allow concurrent access across DIFFERENT clientIds
    public static Nitrite getDatabaseForClient(String clientId) {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("Client ID cannot be null or empty");
        }

        // ConcurrentHashMap handles thread safety per key fine on its own
        return dbRegistry.compute(clientId, (id, db) -> {
            if (db == null || db.isClosed()) {
                return initDatabaseForClient(id);
            }
            return db;
        });
    }

    private static Nitrite initDatabaseForClient(String clientId) {
        String dbFilePath = BASE_DB_DIR + clientId + "-db.db";
        File dbFile = new File(dbFilePath);
        File parentDir = dbFile.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created && !parentDir.exists()) {
                throw new IllegalStateException("Failed to create database directory: " + parentDir.getAbsolutePath());
            }
        }

        MVStoreModule storeModule = MVStoreModule.withConfig()
                .filePath(dbFilePath)
                .compress(true)
                .build();

        JacksonMapperModule jacksonMapperModule = new JacksonMapperModule(new JavaTimeModule());

        return Nitrite.builder()
                .loadModule(storeModule)
                .loadModule(jacksonMapperModule)
                .openOrCreate();
    }

    public static void closeAll() {
        dbRegistry.forEach((clientId, db) -> {
            if (db != null && !db.isClosed()) {
                db.close();
            }
        });
        dbRegistry.clear();
    }

    public static void closeClient(String clientId) {
        if (clientId == null)
            return;

        // computeIfPresent ensures atomic remove & close execution
        dbRegistry.computeIfPresent(clientId, (id, db) -> {
            if (!db.isClosed()) {
                db.close();
            }
            return null;
        });
    }
}
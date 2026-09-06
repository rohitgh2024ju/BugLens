package dev.rohit.buglens.ClientLifecycleEngine.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import dev.rohit.buglens.ClientLifecycleEngine.model.ClientSession;
import dev.rohit.buglens.QueryLayer.config.NitriteMultiTenantConfig;

@Service
public class ClientLifecycleService {
    private static final Map<String, ClientSession> sessions = new ConcurrentHashMap<>();

    private static final Duration DEFAULT_TTL = Duration.ofMinutes(30);

    public ClientSession registerClient(String clientId) {
        Instant now = Instant.now();

        ClientSession session = new ClientSession(
                clientId, now, now, now.plus(DEFAULT_TTL));

        sessions.put(clientId, session);

        return session;
    }

    public void refreshClient(String clientId) {
        ClientSession session = sessions.get(clientId);
        if (session == null) {
            return;
        }

        Instant now = Instant.now();

        session.setLastAccessedAt(now);
        session.setExpiredAt(now.plus(DEFAULT_TTL));
    }

    public void removeClient(String clientId) {
        if (clientId == null || clientId.isBlank()) {
            return;
        }
        try {
            NitriteMultiTenantConfig.deleteClientDatabase(clientId);
            deleteCollectedLog(clientId);
            deleteClientUploads(clientId);

            sessions.remove(clientId);
            System.out.println(
                    "Successfully cleaned up client: " + clientId);

        } catch (Exception e) {
            System.err.println(
                    "Failed to clean up client: " + clientId);
            e.printStackTrace();
        }
    }

    public boolean isExpired(String clientId) {
        ClientSession session = sessions.get(clientId);

        if (session == null) {
            return true;
        }
        return Instant.now()
                .isAfter(session.getExpiredAt());
    }

    public void cleanupExpiredClients() {
        for (Map.Entry<String, ClientSession> entry : sessions.entrySet()) {
            String clientId = entry.getKey();

            if (isExpired(clientId)) {
                removeClient(clientId);
            }
        }
    }

    private void deleteCollectedLog(String clientId) {
        Path logPath = Path.of("buglens/logs", "output-" + clientId + ".jsonl");
        try {
            Files.deleteIfExists(logPath);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to delete collected log for client: " + clientId, e);
        }
    }

    private void deleteClientUploads(String clientId) {
        Path uploadDirectory = Path.of("buglens/uploads", clientId);
        try {
            if (Files.exists(uploadDirectory)) {

                try (var paths = Files.walk(uploadDirectory)) {
                    paths
                            .sorted((first, second) -> second.compareTo(first))
                            .forEach(path -> {
                                try {
                                    Files.deleteIfExists(path);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            });
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to delete uploads for client: "
                            + clientId,
                    e);
        }
    }

    public ClientSession getClientSession(String clientId) {
        return sessions.get(clientId);
    }
}

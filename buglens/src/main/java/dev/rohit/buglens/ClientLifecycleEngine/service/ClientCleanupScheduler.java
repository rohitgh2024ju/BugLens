package dev.rohit.buglens.ClientLifecycleEngine.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component 
public class ClientCleanupScheduler {
    private final ClientLifecycleService clientLifecycleService;

    public ClientCleanupScheduler(
        ClientLifecycleService clientLifecycleService
    ) {
        this.clientLifecycleService = clientLifecycleService;
    }

    @Scheduled(fixedRateString = "PT5M")
    public void cleanupExpiredClients() {
        clientLifecycleService.cleanupExpiredClients();
    }
}

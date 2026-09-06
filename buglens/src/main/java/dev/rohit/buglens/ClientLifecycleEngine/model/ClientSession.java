package dev.rohit.buglens.ClientLifecycleEngine.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
public class ClientSession {
    private String clientId;
    private Instant createdAt;
    private Instant lastAccessedAt;
    private Instant expiredAt;
}

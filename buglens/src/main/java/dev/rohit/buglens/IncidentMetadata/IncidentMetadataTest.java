package dev.rohit.buglens.IncidentMetadata;

import java.time.Instant;
import java.util.*;

import dev.rohit.buglens.IncidentEngine.model.FailureContext;
import dev.rohit.buglens.IncidentEngine.model.Incident;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;

public class IncidentMetadataTest {
    public static void main(String[] args) {
        NormalizedEvent failureEvent = NormalizedEvent.builder()
                .id("EVT-4B72A91C-6D83-42F1-A8E7-91C53D20B644")
                .timestamp(Instant.parse("2026-10-04T08:17:23Z"))
                .source(new HashMap<>(Map.of(
                        "service", "inventory-service",
                        "host", "inventory-server-02")))
                .occurrence(new HashMap<>(Map.of(
                        "exception", "HikariPool-1 - Connection is not available",
                        "level", "ERROR")))
                .context(new HashMap<>(Map.of(
                        "message",
                        "Timeout after 30000ms waiting for a database connection")))
                .metadata(new HashMap<>(Map.of(
                        "requestId", "req-019",
                        "database", "inventory-db")))
                .build();


        NormalizedEvent relatedEvent1 = NormalizedEvent.builder()
                .id("EVT-93C18F52-71A4-4B29-B6D3-5E827F10A921")
                .timestamp(Instant.parse("2026-10-04T08:17:10Z"))
                .source(new HashMap<>(Map.of(
                        "service", "inventory-service")))
                .occurrence(new HashMap<>(Map.of(
                        "exception", "SQLTimeoutException",
                        "level", "WARN")))
                .context(new HashMap<>(Map.of(
                        "message",
                        "Database query exceeded configured timeout")))
                .metadata(new HashMap<>(Map.of(
                        "requestId", "req-019")))
                .build();

        NormalizedEvent relatedEvent2 = NormalizedEvent.builder()
                .id("EVT-2A64D9E7-35F1-48C2-91B6-7E04A83F512C")
                .timestamp(Instant.parse("2026-10-04T08:17:25Z"))
                .source(new HashMap<>(Map.of(
                        "service", "inventory-service")))
                .occurrence(new HashMap<>(Map.of(
                        "exception", "InventoryFetchException",
                        "level", "ERROR")))
                .context(new HashMap<>(Map.of(
                        "message",
                        "Unable to retrieve inventory data")))
                .metadata(new HashMap<>(Map.of(
                        "requestId", "req-019")))
                .build();

        FailureContext failureContext = new FailureContext(
                failureEvent,
                new LinkedHashSet<>(Arrays.asList(
                        relatedEvent1,
                        relatedEvent2)));

        Incident incident = new Incident(
                "INC-002",
                null,
                new LinkedHashSet<>(Collections.singletonList(
                        failureContext)));

        IncidentMetadataService service = new IncidentMetadataService();
        IncidentMetadata metadata = service.generate(incident);

        System.out.println("=================================");
        System.out.println("     INCIDENT METADATA TEST");
        System.out.println("=================================");

        System.out.println("Title          : " + metadata.getTitle());
        System.out.println("Description    : " + metadata.getDescription());
        System.out.println("Service        : " + metadata.getService());
        System.out.println("Exception Type : " + metadata.getExceptionType());
        System.out.println("Tags           : " + metadata.getTags());

        System.out.println("=================================");
    }
}

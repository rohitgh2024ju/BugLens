package dev.rohit.buglens.Application;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.rohit.buglens.Dashboard.DashboardResponse;
import dev.rohit.buglens.IncidentEngine.model.FailureContext;
import dev.rohit.buglens.IncidentEngine.model.Incident;
import dev.rohit.buglens.IncidentMetadata.IncidentMetadata;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;

import org.junit.jupiter.api.Test;

class AnalysisResponseSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void shouldSerializeAndDeserializeAnalysisResponse()
            throws Exception {

        Instant timestamp = Instant.parse("2026-10-24T10:00:00Z");

        NormalizedEvent failureEvent = NormalizedEvent.builder()
                .id("event-001")
                .timestamp(timestamp)
                .source(Map.of("service", "payment-service"))
                .occurrence(Map.of("level", "ERROR"))
                .context(Map.of("message", "Connection refused"))
                .metadata(Map.of("exceptionType", "ConnectException"))
                .build();

        NormalizedEvent relatedEvent = NormalizedEvent.builder()
                .id("event-002")
                .timestamp(timestamp.minusSeconds(5))
                .source(Map.of("service", "payment-service"))
                .occurrence(Map.of("level", "INFO"))
                .context(Map.of("message", "Request started"))
                .metadata(Map.of())
                .build();

        FailureContext failureContext = new FailureContext(
                failureEvent,
                Set.of(relatedEvent));

        Incident incident = new Incident(
                "incident-001",
                new IncidentMetadata(
                        "Payment failure",
                        "Payment connection was refused",
                        "payment-service",
                        "ConnectException",
                        List.of("network", "payment")),
                Set.of(failureContext));

        DashboardResponse dashboard = new DashboardResponse(
                2, 1, 1, 1, 0, 1,
                List.of(),
                List.of());

        AnalysisResponse original =
                new AnalysisResponse(dashboard, List.of(incident));

        String json = objectMapper.writeValueAsString(original);

        AnalysisResponse restored = objectMapper.readValue(
                json, AnalysisResponse.class);

        assertNotNull(restored);
        assertEquals(2, restored.getDashboard().getTotalEvents());
        assertEquals(1, restored.getIncidents().size());

        Incident restoredIncident = restored.getIncidents().get(0);

        assertEquals("incident-001", restoredIncident.getId());
        assertEquals(
                "Payment failure",
                restoredIncident.getMetadata().getTitle());

        FailureContext restoredContext =
                restoredIncident.getFailureContexts()
                        .iterator()
                        .next();

        assertEquals(
                "event-001",
                restoredContext.getFailureEvent().getId());

        assertEquals(
                timestamp,
                restoredContext.getFailureEvent().getTimestamp());

        assertEquals(
                "Connection refused",
                restoredContext.getFailureEvent()
                        .getContext().get("message"));

        assertEquals(
                1,
                restoredContext.getRelatedEvents().size());

        assertEquals(
                "event-002",
                restoredContext.getRelatedEvents()
                        .iterator()
                        .next()
                        .getId());
    }
}
package dev.rohit.buglens.CorrelationEngine.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;

public class CorrelationService {

    public Map<String, List<NormalizedEvent>> groupByRequestId(List<NormalizedEvent> events) {
        if (events == null)
            return Map.of();

        return events.stream()
                .filter(Objects::nonNull)
                .filter(event -> event.getContext() != null)
                .filter(event -> event.getContext().get("requestId") != null)
                .collect(Collectors.groupingBy(
                        event -> event.getContext().get("requestId").toString()));
    }

    public Map<String, List<NormalizedEvent>> groupByTraceId(List<NormalizedEvent> events) {
        if (events == null)
            return Map.of();

        return events.stream()
                .filter(Objects::nonNull)
                .filter(event -> event.getContext() != null)
                .filter(event -> event.getContext().get("traceId") != null)
                .collect(Collectors.groupingBy(
                        event -> event.getContext().get("traceId").toString()));
    }

    public List<List<NormalizedEvent>> groupByTime(List<NormalizedEvent> events, long seconds) {
        if (events == null || events.isEmpty()) {
            return List.of();
        }

        List<NormalizedEvent> sorted = events.stream()
                .filter(Objects::nonNull)
                .filter(event -> event.getTimestamp() != null)
                .sorted(Comparator.comparing(NormalizedEvent::getTimestamp))
                .toList();

        if (sorted.isEmpty())
            return List.of();

        List<List<NormalizedEvent>> groups = new ArrayList<>();
        List<NormalizedEvent> currentGroup = new ArrayList<>();
        Instant groupStartTimestamp = null;

        for (NormalizedEvent event : sorted) {
            Instant currentTimestamp = event.getTimestamp();

            if (groupStartTimestamp == null ||
                    Duration.between(groupStartTimestamp, currentTimestamp).getSeconds() <= seconds) {

                if (currentGroup.isEmpty()) {
                    groupStartTimestamp = currentTimestamp;
                }
                currentGroup.add(event);
            } else {
                groups.add(currentGroup);
                currentGroup = new ArrayList<>();
                currentGroup.add(event);
                groupStartTimestamp = currentTimestamp;
            }
        }

        if (!currentGroup.isEmpty()) {
            groups.add(currentGroup);
        }

        return groups;
    }
}
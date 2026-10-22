package dev.rohit.buglens.Dashboard;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import dev.rohit.buglens.IncidentEngine.model.Incident;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;
import dev.rohit.buglens.IncidentEngine.model.FailureContext;

@Service
public class DashboardService {

    public DashboardResponse getDashboard(List<Incident> incidents) {
        DashboardResponse response = new DashboardResponse();

        if (incidents == null || incidents.isEmpty()) {
            return response;
        }

        response.setDetectedIncidents(incidents.size());

        List<NormalizedEvent> events = collectEvents(incidents);
        response.setTotalEvents(events.size());

        long errorEvents = events.stream()
                .filter(event -> {
                    Object severity = event.getOccurrence().get("severity");

                    if (severity == null) {
                        return false;
                    }

                    String value = severity.toString().toUpperCase();

                    return value.equals("ERROR") || value.equals("FATAL");
                })
                .count();

        long criticalEvents = events.stream()
                .filter(event -> {
                    Object severity = event.getOccurrence().get("severity");

                    return severity != null
                            && severity.toString().equalsIgnoreCase("CRITICAL");
                })
                .count();

        response.setErrorEvents(errorEvents);
        response.setCriticalEvents(criticalEvents);
        response.setSeverityBreakdown(
                calculateSeverityBreakdown(events)

        );
        List<ServiceBreakdown> topServices = calculateTopAffectedServices(events);

        response.setTopAffectedServices(topServices);

        long affectedServices = events.stream()
                .map(event -> event.getSource().get("service"))
                .filter(service -> service != null)
                .map(Object::toString)
                .distinct()
                .count();

        response.setAffectedServices(affectedServices);
        response.setCorrelations(
                calculateCorrelations(incidents));

        return response;
    }

    private List<NormalizedEvent> collectEvents(List<Incident> incidents) {
        Map<String, NormalizedEvent> events = new HashMap<>();

        for (Incident incident : incidents) {

            if (incident.getFailureContexts() == null) {
                continue;
            }

            for (FailureContext context : incident.getFailureContexts()) {

                NormalizedEvent failureEvent = context.getFailureEvent();

                if (failureEvent != null) {
                    events.put(failureEvent.getId(), failureEvent);
                }

                if (context.getRelatedEvents() != null) {
                    for (NormalizedEvent event : context.getRelatedEvents()) {
                        if (event != null) {
                            events.put(event.getId(), event);
                        }
                    }
                }
            }
        }

        return new ArrayList<>(events.values());
    }

    private List<SeverityBreakdown> calculateSeverityBreakdown(
            List<NormalizedEvent> events) {

        Map<String, Long> counts = new HashMap<>();

        for (NormalizedEvent event : events) {

            Object severity = event.getOccurrence().get("severity");

            if (severity == null) {
                continue;
            }

            String name = severity.toString();

            counts.put(
                    name,
                    counts.getOrDefault(name, 0L) + 1);
        }

        long total = events.size();

        return counts.entrySet()
                .stream()
                .map(entry -> {

                    double percentage = total == 0
                            ? 0
                            : Math.round((entry.getValue() * 10000.0) / total) / 100.0;

                    return new SeverityBreakdown(
                            entry.getKey(),
                            entry.getValue(),
                            percentage);
                })
                .toList();
    }

    private List<ServiceBreakdown> calculateTopAffectedServices(
            List<NormalizedEvent> events) {

        Map<String, Long> serviceCounts = new HashMap<>();

        for (NormalizedEvent event : events) {

            Object severity = event.getOccurrence().get("severity");

            if (severity == null) {
                continue;
            }

            String severityValue = severity.toString().toUpperCase();

            if (!severityValue.equals("ERROR")
                    && !severityValue.equals("FATAL")) {
                continue;
            }

            Object component = event.getSource().get("component");

            if (component == null) {
                continue;
            }

            String componentName = component.toString();
            String serviceName = extractServiceName(componentName);

            if (serviceName == null || serviceName.isBlank()) {
                continue;
            }

            serviceCounts.put(
                    serviceName,
                    serviceCounts.getOrDefault(serviceName, 0L) + 1);
        }

        return serviceCounts.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(4)
                .map(entry -> new ServiceBreakdown(
                        entry.getKey(),
                        entry.getValue()))
                .toList();
    }

    private String extractServiceName(String component) {

        if (component == null || component.isBlank()) {
            return null;
        }

        int lastDot = component.lastIndexOf('.');

        String className = lastDot >= 0
                ? component.substring(lastDot + 1)
                : component;

        if (className.endsWith("Service")) {
            className = className.substring(
                    0,
                    className.length() - "Service".length());
        }

        return className
                .replaceAll("([a-z])([A-Z])", "$1-$2")
                .toLowerCase();
    }

    private long calculateCorrelations(List<Incident> incidents) {
        long count = 0;

        for (Incident incident : incidents) {
            if (incident.getFailureContexts() == null) {
                continue;
            }

            count += incident.getFailureContexts().size();
        }

        return count;
    }
}
package dev.rohit.buglens.IncidentMetadata;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import dev.rohit.buglens.IncidentEngine.model.FailureContext;
import dev.rohit.buglens.IncidentEngine.model.Incident;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;

@Service
public class IncidentMetadataService {
    public IncidentMetadata generate(Incident incident) {
        if (incident == null) {
            throw new IllegalArgumentException("incident cannot be null");
        }

        String service = fetchService(incident);
        String exceptionType = fetchExceptionType(incident);
        List<String> tags = fetchTags(incident);
        String title = fetchTitle(service, tags);
        String description = fetchDescription(incident, service);

        return new IncidentMetadata(title, description, service, exceptionType, tags);
    }

    private String fetchService(Incident incident) {
        return incident.getFailureContexts()
                .stream()
                .findFirst()
                .map(FailureContext::getFailureEvent)
                .map(event -> event.getSource().get("component"))
                .map(Object::toString)
                .map(this::extractServiceName)
                .orElse("SERVICE UNKNOWN");
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

    private String fetchExceptionType(Incident incident) {
        return incident.getFailureContexts()
                .stream()
                .findFirst()
                .map(FailureContext::getFailureEvent)
                .map(event -> event.getOccurrence().get("exception"))
                .map(Object::toString)
                .orElse("EXCEPTIONTYPE UNKNOWN");
    }

    private static final Set<String> TAG_VOCABULARY = new HashSet<>(Set.of(
            "database",
            "connection",
            "timeout",
            "query",
            "cache",
            "network",
            "authentication",
            "authorization",
            "api",
            "request",
            "response",
            "server",
            "memory",
            "disk",
            "cpu",
            "thread",
            "deadlock",
            "lock",
            "transaction",
            "queue"));

    private List<String> fetchTags(Incident incident) {
        Set<String> tags = new LinkedHashSet<>();

        incident.getFailureContexts().forEach(context -> {

            NormalizedEvent failureEvent = context.getFailureEvent();

            if (failureEvent != null) {
                extractKeywordTags(failureEvent, tags);
            }

            if (context.getRelatedEvents() != null) {
                context.getRelatedEvents()
                        .forEach(event -> extractKeywordTags(event, tags));
            }
        });

        return tags.stream()
                .limit(5)
                .toList();
    }

    private void extractKeywordTags(
            NormalizedEvent event,
            Set<String> tags) {

        if (event == null) {
            return;
        }

        extractKeywordTags(event.getOccurrence(), tags);
        extractKeywordTags(event.getContext(), tags);
        extractKeywordTags(event.getMetadata(), tags);
    }

    private void extractKeywordTags(
            Object value,
            Set<String> tags) {

        if (value == null) {
            return;
        }

        String[] words = value
                .toString()
                .toLowerCase()
                .split("[^a-z0-9]+");

        for (String word : words) {

            if (TAG_VOCABULARY.contains(word)) {
                tags.add(word);
            }
        }
    }

    private String fetchTitle(String service, List<String> tags) {
        String serviceName = formatServiceName(service);

        String problem = tags.stream()
                .filter(tag -> !tag.equals("api"))
                .limit(3)
                .map(this::capitalize)
                .collect(Collectors.joining(" "));

        if (!serviceName.isBlank() && !problem.isBlank()) {
            return serviceName + " " + problem + " Failure";
        }

        if (!serviceName.isBlank()) {
            return serviceName + " Failure";
        }

        if (!problem.isBlank()) {
            return problem + " Failure";
        }

        return "Incident Detected";
    }

    private String formatServiceName(String service) {
        if (service == null || service.isBlank()) {
            return "";
        }

        return Arrays.stream(service.split("-"))
                .filter(part -> !part.isBlank())
                .map(this::capitalize)
                .collect(Collectors.joining(" "));
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return Character.toUpperCase(value.charAt(0))
                + value.substring(1);
    }

    private String fetchDescription(Incident incident, String service) {
        String message = incident.getFailureContexts()
                .stream()
                .findFirst()
                .map(FailureContext::getFailureEvent)
                .map(event -> event.getOccurrence().get("message"))
                .map(Object::toString)
                .orElse("DESCRIPTION UNKNOWN");

        if (message == null || message.isBlank()) {
            return null;
        }

        String serviceName = formatServiceName(service);

        if (!serviceName.isBlank()) {
            return "The " + serviceName.toLowerCase()
                    + " " + message.toLowerCase();
        }

        return message;
    }
}

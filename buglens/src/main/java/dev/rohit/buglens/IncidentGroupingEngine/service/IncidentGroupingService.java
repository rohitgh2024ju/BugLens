package dev.rohit.buglens.IncidentGroupingEngine.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import dev.rohit.buglens.IncidentEngine.model.FailureContext;
import dev.rohit.buglens.IncidentEngine.model.Incident;
import dev.rohit.buglens.IncidentGroupingEngine.model.IncidentGroup;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;

public class IncidentGroupingService {

    private static final double FAILURE_WEIGHT = 0.70;
    private static final double CONTEXT_WEIGHT = 0.30;
    private static final double GROUPING_THRESHOLD = 0.60;

    private final List<IncidentGroup> incidentGroups = new ArrayList<>();

    public List<IncidentGroup> buildGroups(List<Incident> incidents) {

        if (incidents == null || incidents.isEmpty()) {
            return incidentGroups;
        }

        for (Incident incident : incidents) {
            IncidentGroup matchingGroup = null;

            for (IncidentGroup group : incidentGroups) {

                if (belongsToGroup(incident, group)) {
                    matchingGroup = group;
                    break;
                }
            }

            if (matchingGroup != null) {

                matchingGroup
                        .getIncidents()
                        .add(incident);

            } else {

                Set<Incident> groupIncidents = new HashSet<>();
                groupIncidents.add(incident);

                IncidentGroup newGroup = IncidentGroup.builder()
                        .groupId(
                                UUID.randomUUID()
                                        .toString())
                        .incidents(groupIncidents)
                        .build();

                incidentGroups.add(newGroup);
            }
        }

        return incidentGroups;
    }

    private boolean belongsToGroup(
            Incident incident,
            IncidentGroup group) {

        Set<String> incidentFailures = getFailureMessages(incident);
        Set<String> incidentContext = getRelatedMessages(incident);

        Set<String> groupFailures = getGroupFailureMessages(group);
        Set<String> groupContext = getGroupRelatedMessages(group);

        double failureSimilarity = calculateJaccard(
                incidentFailures,
                groupFailures);

        double contextSimilarity = calculateJaccard(
                incidentContext,
                groupContext);

        double overallSimilarity = (failureSimilarity * FAILURE_WEIGHT)
                + (contextSimilarity * CONTEXT_WEIGHT);

        return overallSimilarity >= GROUPING_THRESHOLD;
    }

    private Set<String> getFailureMessages(
            Incident incident) {

        Set<String> failureMessages = new HashSet<>();

        if (incident == null
                || incident.getFailureContexts() == null) {

            return failureMessages;
        }

        for (FailureContext context : incident.getFailureContexts()) {

            if (context == null
                    || context.getFailureEvent() == null) {

                continue;
            }

            String message = extractMessage(
                    context.getFailureEvent());

            if (message != null) {
                failureMessages.add(message);
            }
        }

        return failureMessages;
    }

    private Set<String> getRelatedMessages(
            Incident incident) {
        Set<String> relatedMessages = new HashSet<>();
        if (incident == null
                || incident.getFailureContexts() == null) {

            return relatedMessages;
        }

        for (FailureContext context : incident.getFailureContexts()) {

            if (context == null
                    || context.getRelatedEvents() == null) {

                continue;
            }

            for (NormalizedEvent event : context.getRelatedEvents()) {

                String message = extractMessage(event);

                if (message != null) {
                    relatedMessages.add(message);
                }
            }
        }

        return relatedMessages;
    }

    private Set<String> getGroupFailureMessages(
            IncidentGroup group) {

        Set<String> failureMessages = new HashSet<>();

        if (group == null
                || group.getIncidents() == null) {

            return failureMessages;
        }

        for (Incident incident : group.getIncidents()) {

            failureMessages.addAll(
                    getFailureMessages(incident));
        }

        return failureMessages;
    }

    private Set<String> getGroupRelatedMessages(
            IncidentGroup group) {
        Set<String> relatedMessages = new HashSet<>();
        if (group == null
                || group.getIncidents() == null) {
            return relatedMessages;
        }

        for (Incident incident : group.getIncidents()) {

            relatedMessages.addAll(
                    getRelatedMessages(incident));
        }

        return relatedMessages;
    }

    private String extractMessage(
            NormalizedEvent event) {
        if (event == null
                || event.getOccurrence() == null) {

            return null;
        }

        Object message = event.getOccurrence()
                .get("message");

        if (message == null) {
            return null;
        }

        return normalizeMessage(
                message.toString());
    }
    private String normalizeMessage(
            String message) {

        if (message == null) {
            return null;
        }
        String normalized = message
                .trim()
                .toLowerCase()
                .replaceAll("\\s+", " ");

        return normalized.isBlank()
                ? null
                : normalized;
    }
    private double calculateJaccard(
            Set<String> first,
            Set<String> second) {

        if (first == null
                || second == null
                || first.isEmpty()
                || second.isEmpty()) {

            return 0.0;
        }
        Set<String> intersection = new HashSet<>(first);
        intersection.retainAll(second);
        Set<String> union = new HashSet<>(first);

        union.addAll(second);

        if (union.isEmpty()) {
            return 0.0;
        }
        return (double) intersection.size()
                / union.size();
    }

    public List<IncidentGroup> getIncidentGroups() {
        return this.incidentGroups;
    }

    public void viewAllGroups() {
        if (this.incidentGroups.isEmpty()) {
            System.out.println(
                    "No incident groups found.");
            return;
        }
        System.out.println(
                "NUMBER OF GROUPS : "
                        + this.incidentGroups.size());
        for (IncidentGroup group : this.incidentGroups) {
            System.out.println(
                    "============================================");
            System.out.println(
                    "INCIDENT GROUP : "
                            + group.getGroupId());
            System.out.println(
                    "TOTAL INCIDENTS : "
                            + group.getIncidents().size());
            System.out.println(
                    "============================================");
            for (Incident incident : group.getIncidents()) {
                System.out.println(
                        "INCIDENT : "
                                + incident.getId());
                System.out.println(
                        "FAILURE CONTEXTS : "
                                + incident
                                        .getFailureContexts()
                                        .size());

                for (FailureContext context : incident.getFailureContexts()) {
                    System.out.println(
                            "FAILURE EVENT : "
                                    + extractMessage(
                                            context.getFailureEvent()));

                    System.out.println(
                            "RELATED EVENTS :");

                    if (context.getRelatedEvents() != null) {
                        for (NormalizedEvent event : context.getRelatedEvents()) {
                            String message = extractMessage(event);
                            if (message != null) {
                                System.out.println(
                                        "  -> "
                                                + message);
                            }
                        }
                    }
                    System.out.println(
                            "----------------------------");
                }
                System.out.println();
            }
            System.out.println(
                    "============================================");
            System.out.println();
        }
    }
}
package dev.rohit.buglens.IncidentEngine.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import dev.rohit.buglens.IncidentEngine.model.FailureContext;
import dev.rohit.buglens.IncidentEngine.model.Incident;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;

public class IncidentService {

    private static final double OVERLAP_THRESHOLD = 0.60;
    private List<Incident> incidents = new ArrayList<>();

    public List<Incident> buildIncidents(
            List<FailureContext> contexts) {
        this.incidents.clear();

        for (FailureContext context : contexts) {
            Set<NormalizedEvent> currentEvents = getAllEvents(context);
            Incident matchingIncident = null;

            for (Incident incident : this.incidents) {

                if (belongsToIncident(
                        currentEvents,
                        incident)) {
                    matchingIncident = incident;
                    break;
                }
            }

            if (matchingIncident != null) {
                matchingIncident
                        .getFailureContexts()
                        .add(context);

            } else {
                Set<FailureContext> incidentContexts = new HashSet<>();
                incidentContexts.add(context);

                Incident incident = new Incident(
                        UUID.randomUUID().toString(),
                        incidentContexts);

                this.incidents.add(incident);
            }
        }

        return this.incidents;
    }

    private boolean belongsToIncident(
            Set<NormalizedEvent> contextEvents,
            Incident incident) {

        Set<NormalizedEvent> incidentEvents = new HashSet<>();

        for (FailureContext context : incident.getFailureContexts()) {

            incidentEvents.addAll(
                    getAllEvents(context));
        }
        double overlap = calculateJaccard(
                contextEvents,
                incidentEvents);

        return overlap >= OVERLAP_THRESHOLD;
    }

    private Set<NormalizedEvent> getAllEvents(
            FailureContext context) {

        Set<NormalizedEvent> events = new HashSet<>(
                context.getRelatedEvents());
        events.add(
                context.getFailureEvent());

        return events;
    }

    private double calculateJaccard(
            Set<NormalizedEvent> first,
            Set<NormalizedEvent> second) {

        Set<NormalizedEvent> intersection = new HashSet<>(first);

        intersection.retainAll(second);

        Set<NormalizedEvent> union = new HashSet<>(first);

        union.addAll(second);

        if (union.isEmpty()) {
            return 0.0;
        }
        return (double) intersection.size()
                / union.size();
    }

    public List<Incident> getIncidents() {
        return incidents;
    }

    public void viewAllIncidents() {
        if (this.incidents == null || this.incidents.isEmpty()) {
            System.out.println("No incidents found");
            return;
        }
        this.incidents.forEach(incident -> {
            System.out.println("================================");
            System.out.println("INCIDENT : " + incident.getId());

            System.out.println("FAILURE CONTEXTS : "
                    + incident.getFailureContexts().size());

            incident.getFailureContexts().forEach(context -> {
                System.out.println(
                        "FAILURE EVENT : "
                                + context.getFailureEvent()
                                        .getOccurrence()
                                        .get("message"));

                System.out.println("RELATED EVENTS :");
                context.getRelatedEvents().forEach(event -> {
                    System.out.println(
                            "  -> "
                                    + event.getOccurrence()
                                            .get("message"));
                });

                System.out.println("----------------");
            });
            System.out.println("================================");
        });
    }
}
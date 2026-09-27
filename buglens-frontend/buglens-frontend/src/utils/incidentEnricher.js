import { calculateIncidentDetails } from "./incidentCalculator.js";

export function enrichIncidents(incidents) {
    return incidents.map((incident) => ({
        ...incident, ...calculateIncidentDetails(incident), status: "open"
    }));
}
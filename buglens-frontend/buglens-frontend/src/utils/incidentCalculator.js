// calculate severity, eventCount, relatedEventCount, timestamp,tags

export function calculateIncidentDetails(incident) {
    const failureContexts = incident.failureContexts || [];

    const failureEvents = failureContexts.map((context) => context.failureEvent).filter(Boolean);
    const relatedEvents = failureContexts.flatMap((context) => context.relatedEvents || []);

    const uniqueEvents = new Map();

    failureContexts.forEach((event) => {
        uniqueEvents.set(event.id, event)
    });

    relatedEvents.forEach((event) => {
        uniqueEvents.set(event.id, event)
    });

    const eventCount = uniqueEvents.size;
    const uniqueRelatedEvents = new Map();

    relatedEvents.forEach((event) => {
        uniqueRelatedEvents.set(event.id, event)
    });

    const relatedEventCount = uniqueRelatedEvents.size;

    const severityCounts = {};

    failureEvents.forEach((event) => {
        const severity = event.occurrence?.severity?.toUpperCase();

        if (!severity) {
            return;
        }

        severityCounts[severity] = (severityCounts[severity] || 0) + 1;
    });

    const severityPriority = [
        "FATAL",
        "ERROR",
        "WARN",
        "INFO",
        "DEBUG",
        "TRACE",
    ];

    let dominantSeverity = null;
    let highestCount = 0;

    severityPriority.forEach((severity) => {

        const count = severityCounts[severity] || 0;

        if (count > highestCount) {

            highestCount = count;
            dominantSeverity = severity;
        }
    });

    const severityMap = {
        FATAL: "critical",
        ERROR: "critical",
        WARN: "medium",
        INFO: "low",
        DEBUG: "low",
        TRACE: "low",
    };

    const severity =
        severityMap[dominantSeverity] || "low";

    const failureTimestamps = failureEvents
        .map((event) => event.timestamp)
        .filter(Boolean)
        .map((timestamp) => new Date(timestamp))
        .filter(
            (date) => !Number.isNaN(date.getTime())
        )
        .sort((a, b) => a - b);

    const timestamp =
        failureTimestamps.length > 0
            ? failureTimestamps[0].toISOString()
            : null;

    return {
        severity,
        eventCount,
        relatedEventCount,
        timestamp,
    };
}
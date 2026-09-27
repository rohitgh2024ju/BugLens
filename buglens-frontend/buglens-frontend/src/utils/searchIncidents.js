export function searchIncidents(incidents, query) {
    const normalizedQuery = query.trim().toLowerCase();

    if (!normalizedQuery) {
        return [];
    }

    return incidents.filter((incident) => {

        const searchableFields = [
            incident.id,
            incident.title,
            incident.description,
            incident.service,
            incident.exception,
            ...(incident.tags || []),
        ];

        const events = (incident.failureContexts || []).flatMap(
            (context) => [
                context.failureEvent,
                ...(context.relatedEvents || []),
            ]
        );

        events.forEach((event) => {
            searchableFields.push(
                event.id,
                event.source?.component,
                event.occurrence?.severity,
                event.occurrence?.message
            );
        });

        return searchableFields
            .filter(Boolean)
            .some((value) =>
                String(value)
                    .toLowerCase()
                    .includes(normalizedQuery)
            );
    });
}
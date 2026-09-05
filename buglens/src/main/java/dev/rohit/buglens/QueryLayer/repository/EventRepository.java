package dev.rohit.buglens.QueryLayer.repository;

import java.util.List;
import java.util.Map;

import static org.dizitart.no2.filters.FluentFilter.where;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.filters.Filter;
import org.dizitart.no2.repository.ObjectRepository;

import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;
import dev.rohit.buglens.QueryLayer.config.NitriteMultiTenantConfig;
import dev.rohit.buglens.QueryLayer.query.EventQuery;
import dev.rohit.buglens.QueryLayer.query.QueryCriteria;

public class EventRepository {

    /**
     * Saves normalized events for a specific client.
     */
    public void saveAll(
            String clientId,
            List<NormalizedEvent> events) {

        validateClientId(clientId);

        if (events == null || events.isEmpty()) {
            return;
        }

        Nitrite db = NitriteMultiTenantConfig
                .getDatabaseForClient(clientId);

        ObjectRepository<NormalizedEvent> repository = db.getRepository(NormalizedEvent.class);

        NormalizedEvent[] eventArray = events.toArray(new NormalizedEvent[0]);

        repository.insert(eventArray);
    }

    /**
     * Deletes all normalized events for a specific client.
     *
     * Since every client has its own Nitrite database,
     * removing all objects from this repository only affects
     * that client's events.
     */
    public void deleteAll(String clientId) {

        validateClientId(clientId);

        Nitrite db = NitriteMultiTenantConfig
                .getDatabaseForClient(clientId);

        ObjectRepository<NormalizedEvent> repository = db.getRepository(NormalizedEvent.class);

        repository.remove(Filter.ALL);
    }

    /**
     * Prints all normalized events for a client.
     */
    public void viewAll(String clientId) {

        validateClientId(clientId);

        Nitrite db = NitriteMultiTenantConfig
                .getDatabaseForClient(clientId);

        ObjectRepository<NormalizedEvent> repository = db.getRepository(NormalizedEvent.class);

        for (NormalizedEvent event : repository.find()) {
            System.out.println(event);
        }
    }

    /**
     * Returns all normalized events for a client.
     */
    public List<NormalizedEvent> getAll(String clientId) {

        validateClientId(clientId);

        Nitrite db = NitriteMultiTenantConfig
                .getDatabaseForClient(clientId);

        ObjectRepository<NormalizedEvent> repository = db.getRepository(NormalizedEvent.class);

        return repository
                .find()
                .toList();
    }

    /**
     * Finds normalized events based on the supplied query.
     */
    public List<NormalizedEvent> find(
            String clientId,
            EventQuery query)
            throws IllegalArgumentException {

        validateClientId(clientId);

        if (query == null) {
            throw new IllegalArgumentException(
                    "Query is required");
        }

        if (query.getCriteria() == null) {
            throw new IllegalArgumentException(
                    "Query criteria is required");
        }

        if (query.getLimit() < 0) {
            throw new IllegalArgumentException(
                    "Limit cannot be negative");
        }

        Nitrite db = NitriteMultiTenantConfig
                .getDatabaseForClient(clientId);

        ObjectRepository<NormalizedEvent> repository = db.getRepository(NormalizedEvent.class);

        Filter filter = buildFilter(query.getCriteria());

        List<NormalizedEvent> events;

        if (filter == null) {

            events = repository
                    .find()
                    .toList();

        } else {

            events = repository
                    .find(filter)
                    .toList();
        }

        /*
         * A limit of 0 means no limit.
         */
        if (query.getLimit() == 0) {
            return events;
        }

        return events.stream()
                .limit(query.getLimit())
                .toList();
    }

    /**
     * Adds filters for fields stored inside a map.
     */
    private Filter addMapFilters(
            Filter existingFilter,
            Map<String, Object> fields,
            String prefix) {

        if (fields == null || fields.isEmpty()) {
            return existingFilter;
        }

        for (Map.Entry<String, Object> entry : fields.entrySet()) {

            Filter condition = where(prefix + "."
                    + entry.getKey())
                    .eq(entry.getValue());

            existingFilter = existingFilter == null
                    ? condition
                    : Filter.and(
                            existingFilter,
                            condition);
        }

        return existingFilter;
    }

    /**
     * Builds a Nitrite filter from query criteria.
     */
    private Filter buildFilter(
            QueryCriteria criteria) {

        Filter filter = null;

        filter = addMapFilters(
                filter,
                criteria.getSource(),
                "source");

        filter = addMapFilters(
                filter,
                criteria.getOccurrence(),
                "occurrence");

        filter = addMapFilters(
                filter,
                criteria.getContext(),
                "context");

        filter = addMapFilters(
                filter,
                criteria.getMetadata(),
                "metadata");

        return filter;
    }

    /**
     * Validates the client identifier.
     */
    private void validateClientId(String clientId) {

        if (clientId == null
                || clientId.isBlank()) {

            throw new IllegalArgumentException(
                    "Client ID is required");
        }
    }
}
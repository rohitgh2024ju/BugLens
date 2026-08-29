package dev.rohit.buglens.QueryLayer.repository;

import java.util.List;
import java.util.Map;

import org.dizitart.no2.filters.Filter;
import static org.dizitart.no2.filters.FluentFilter.where;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.repository.ObjectRepository;

import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;
import dev.rohit.buglens.QueryLayer.config.NitriteMultiTenantConfig;
import dev.rohit.buglens.QueryLayer.query.EventQuery;
import dev.rohit.buglens.QueryLayer.query.QueryCriteria;

public class EventRepository {
    public void saveAll(String clientId, List<NormalizedEvent> events) {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("Client ID is required");
        }

        if (events == null || events.isEmpty()) {
            return;
        }

        Nitrite db = NitriteMultiTenantConfig.getDatabaseForClient(clientId);
        ObjectRepository<NormalizedEvent> repository = db.getRepository(NormalizedEvent.class);

        NormalizedEvent[] eventArray = events.toArray(new NormalizedEvent[0]);

        repository.insert(eventArray);
    }

    public void viewAll(String clientId) throws IllegalAccessException {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalAccessException("clientId is required");
        }
        Nitrite db = NitriteMultiTenantConfig.getDatabaseForClient(clientId);

        ObjectRepository<NormalizedEvent> repository = db.getRepository(NormalizedEvent.class);

        for (NormalizedEvent event : repository.find()) {
            System.out.println(event);
        }
    }

    public List<NormalizedEvent> getAll(String clientId) throws IllegalAccessException {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalAccessException("clientId is required");
        }
        Nitrite db = NitriteMultiTenantConfig.getDatabaseForClient(clientId);

        ObjectRepository<NormalizedEvent> repository = db.getRepository(NormalizedEvent.class);

        List<NormalizedEvent> events = repository.find().toList();

        return events;
    }

    public List<NormalizedEvent> find(String clientId, EventQuery query)
            throws IllegalArgumentException, IllegalAccessException {

        if (clientId == null || clientId.isBlank()) {
            throw new IllegalAccessException("clientId is required");
        }

        if (query == null) {
            throw new IllegalArgumentException("query is required");
        }

        if (query.getCriteria() == null) {
            throw new IllegalArgumentException("query criteria is required");
        }

        if (query.getLimit() < 0) {
            throw new IllegalArgumentException("limit cannot be negative");
        }

        Nitrite db = NitriteMultiTenantConfig.getDatabaseForClient(clientId);
        ObjectRepository<NormalizedEvent> repository = db.getRepository(NormalizedEvent.class);

        Filter filter = buildFilter(query.getCriteria());

        List<NormalizedEvent> events;

        if (filter == null) {
            events = repository.find().toList();
        } else {
            events = repository.find(filter).toList();
        }

        if (query.getLimit() == 0) {
            return events;
        }

        return events.stream().limit(query.getLimit()).toList();
    }

    private Filter addMapFilters(
            Filter existingFilter,
            Map<String, Object> fields,
            String prefix) {
        if (fields == null) {
            return existingFilter;
        }

        for (Map.Entry<String, Object> entry : fields.entrySet()) {

            Filter condition = where(prefix + "." + entry.getKey())
                    .eq(entry.getValue());

            existingFilter = existingFilter == null
                    ? condition
                    : Filter.and(existingFilter, condition);
        }

        return existingFilter;
    }

    private Filter buildFilter(QueryCriteria criteria) {

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
}
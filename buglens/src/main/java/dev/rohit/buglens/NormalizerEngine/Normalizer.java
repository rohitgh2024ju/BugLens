package dev.rohit.buglens.NormalizerEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;
import dev.rohit.buglens.ParserEngine.ParserEngine;

// Takes parsed data + mapping data
// Creates the normalized Event
public class Normalizer {
    private final String parserClass;
    private final String inputPath;

    public Normalizer(String parserClass, String inputPath) {
        this.parserClass = parserClass;
        this.inputPath = inputPath;
    }

    public List<NormalizedEvent> normalize() {
        List<NormalizedEvent> normalizedEvents = new ArrayList<>();

        try {
            String parserId = switch (parserClass) {
                case "SpringBootParser" -> "spring_boot";
                default -> "unknown";
            };

            ParserEngine parserEngine = new ParserEngine(inputPath, parserClass);
            MappingLoader mappingLoader = new MappingLoader();

            List<FieldMapping> classMapping = mappingLoader.loadMapper(parserId);
            JSONArray parsedLogArray = parserEngine.runParser();

            if (parsedLogArray == null) {
                return normalizedEvents;
            }

            for (Object item : parsedLogArray) {
                if (item instanceof JSONObject log) {

                    NormalizedEvent event = new NormalizedEvent();
                    event.setId(UUID.randomUUID().toString());

                    for (FieldMapping mapping : classMapping) {
                        String sourceKey = mapping.getSource();
                        String targetKey = mapping.getTarget();

                        // FIX: check sourceKey for nullability instead of targetKey
                        if (log.has(sourceKey) && !log.isNull(sourceKey)) {
                            Object value = log.get(sourceKey);

                            if ("timestamp".equals(targetKey)) {
                                event.setTimestamp(value.toString());
                            } else if (targetKey.contains(".")) {
                                String[] parts = targetKey.split("\\.", 2);
                                event.putField(parts[0], parts[1], value);
                            } else {
                                event.putField("metadata", targetKey, value);
                            }
                        }
                    }

                    normalizedEvents.add(event);
                }
            }

        } catch (Exception e) {
            System.err.println("Error while normalizing logs: " + e.getMessage());
            e.printStackTrace();
        }

        return normalizedEvents;
    }
}
package dev.rohit.buglens.NormalizerEngine;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;
import dev.rohit.buglens.ParserEngine.ParserEngine;

public class Normalizer {

    private final String parserClass;
    private final Path inputPath;

    public Normalizer(
            String parserClass,
            Path inputPath) {

        if (parserClass == null
                || parserClass.isBlank()) {

            throw new IllegalArgumentException(
                    "Parser class is required");
        }

        if (inputPath == null) {

            throw new IllegalArgumentException(
                    "Input path is required");
        }

        this.parserClass = parserClass;
        this.inputPath = inputPath;
    }

    public List<NormalizedEvent> normalize() {

        List<NormalizedEvent> normalizedEvents =
                new ArrayList<>();

        try {

            String parserId = switch (parserClass) {

                case "SpringBootParser" -> "spring_boot";

                default -> "unknown";
            };

            ParserEngine parserEngine =
                    new ParserEngine(
                            inputPath,
                            parserClass);

            MappingLoader mappingLoader =
                    new MappingLoader();

            List<FieldMapping> classMapping =
                    mappingLoader.loadMapper(
                            parserId);

            JSONArray parsedLogArray =
                    parserEngine.runParser();

            if (parsedLogArray == null
                    || parsedLogArray.isEmpty()) {

                return normalizedEvents;
            }

            for (Object item : parsedLogArray) {

                if (!(item instanceof JSONObject log)) {
                    continue;
                }

                NormalizedEvent event =
                        new NormalizedEvent();

                event.setId(
                        UUID.randomUUID()
                                .toString());

                for (FieldMapping mapping
                        : classMapping) {

                    String sourceKey =
                            mapping.getSource();

                    String targetKey =
                            mapping.getTarget();

                    if (!log.has(sourceKey)
                            || log.isNull(sourceKey)) {

                        continue;
                    }

                    Object value =
                            convertJsonValue(
                                    log.get(sourceKey));

                    if ("timestamp"
                            .equals(targetKey)) {

                        event.setTimestamp(
                                (Instant) value);

                    } else if (
                            targetKey.contains(".")) {

                        String[] parts =
                                targetKey.split(
                                        "\\.",
                                        2);

                        event.putField(
                                parts[0],
                                parts[1],
                                value);

                    } else {

                        event.putField(
                                "metadata",
                                targetKey,
                                value);
                    }
                }

                normalizedEvents.add(event);
            }

        } catch (Exception e) {

            System.err.println(
                    "Error while normalizing logs: "
                            + e.getMessage());

            e.printStackTrace();
        }

        return normalizedEvents;
    }

    private Object convertJsonValue(Object value) {
        if (value instanceof JSONObject jsonObject) {

            Map<String, Object> map =
                    new HashMap<>();

            for (String key
                    : jsonObject.keySet()) {

                map.put(
                        key,
                        convertJsonValue(
                                jsonObject.get(key)));
            }

            return map;
        }

        if (value instanceof JSONArray jsonArray) {

            List<Object> list =
                    new ArrayList<>();

            for (int i = 0;
                    i < jsonArray.length();
                    i++) {

                list.add(
                        convertJsonValue(
                                jsonArray.get(i)));
            }
            return list;
        }
        return value;
    }
}
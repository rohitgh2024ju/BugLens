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

import dev.rohit.buglens.IngestionEngine.format.LogFormat;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;
import dev.rohit.buglens.ParserEngine.ParserEngine;

public class Normalizer {

    private final LogFormat logFormat;
    private final Path inputPath;

    public Normalizer(
            LogFormat logFormat,
            Path inputPath) {

        if (logFormat == null) {
            throw new IllegalArgumentException(
                    "Log format is required"
            );
        }

        if (inputPath == null) {
            throw new IllegalArgumentException(
                    "Input path is required"
            );
        }

        this.logFormat = logFormat;
        this.inputPath = inputPath;
    }

    public List<NormalizedEvent> normalize() {

        List<NormalizedEvent> normalizedEvents =
                new ArrayList<>();

        try {

            /*
             * --------------------------------------------------
             * 1. GET FORMAT ID
             * --------------------------------------------------
             *
             * Example:
             *
             * formatId = "spring_boot"
             *
             * This identifies the log format and is used
             * by the MappingLoader.
             */
            String formatId =
                    logFormat.getFormatId();

            /*
             * --------------------------------------------------
             * 2. PARSE LOGS
             * --------------------------------------------------
             *
             * ParserEngine uses the parser information inside
             * LogFormat to select the correct BLR parser.
             */
            ParserEngine parserEngine =
                    new ParserEngine(inputPath);

            JSONArray parsedLogArray =
                    parserEngine.runParser(logFormat);

            /*
             * --------------------------------------------------
             * 3. LOAD NORMALIZATION MAPPINGS
             * --------------------------------------------------
             *
             * IMPORTANT:
             *
             * MappingLoader needs the FORMAT ID:
             *
             *     spring_boot
             *
             * NOT:
             *
             *     SpringBootParser
             */
            MappingLoader mappingLoader =
                    new MappingLoader();

            List<FieldMapping> classMapping =
                    mappingLoader.loadMapper(formatId);

            /*
             * --------------------------------------------------
             * 4. PRINT DEBUG INFORMATION
             * --------------------------------------------------
             */

            System.out.println(
                    "FORMAT ID: "
                            + formatId
            );

            System.out.println(
                    "FORMAT NAME: "
                            + logFormat.getName()
            );

            System.out.println(
                    "PARSER: "
                            + logFormat.getParser()
            );

            System.out.println(
                    "MAPPING COUNT: "
                            + classMapping.size()
            );

            classMapping.forEach(mapping ->
                    System.out.println(
                            mapping.getSource()
                                    + " -> "
                                    + mapping.getTarget()
                    )
            );

            System.out.println(
                    "PARSED LOG COUNT: "
                            + parsedLogArray.length()
            );

            /*
             * --------------------------------------------------
             * 5. SHOW FIRST PARSED LOG
             * --------------------------------------------------
             */

            if (!parsedLogArray.isEmpty()) {

                System.out.println(
                        "FIRST PARSED LOG: "
                                + parsedLogArray
                                        .getJSONObject(0)
                                        .toString(2)
                );
            }

            /*
             * --------------------------------------------------
             * 6. NO PARSED LOGS
             * --------------------------------------------------
             */

            if (parsedLogArray.isEmpty()) {
                return normalizedEvents;
            }

            /*
             * --------------------------------------------------
             * 7. NORMALIZE EACH PARSED LOG
             * --------------------------------------------------
             */

            for (Object item : parsedLogArray) {

                if (!(item instanceof JSONObject log)) {
                    continue;
                }

                NormalizedEvent event =
                        new NormalizedEvent();

                /*
                 * Generate a unique ID for the normalized event.
                 */
                event.setId(
                        UUID.randomUUID().toString()
                );

                /*
                 * --------------------------------------------------
                 * APPLY FIELD MAPPINGS
                 * --------------------------------------------------
                 */

                for (FieldMapping mapping :
                        classMapping) {

                    String sourceKey =
                            mapping.getSource();

                    String targetKey =
                            mapping.getTarget();

                    /*
                     * Skip fields that are not present
                     * in the parsed log.
                     */
                    if (!log.has(sourceKey)
                            || log.isNull(sourceKey)) {

                        continue;
                    }

                    Object value =
                            convertJsonValue(
                                    log.get(sourceKey)
                            );

                    /*
                     * --------------------------------------------------
                     * TIMESTAMP
                     * --------------------------------------------------
                     */

                    if ("timestamp".equals(targetKey)) {

                        event.setTimestamp(
                                (Instant) value
                        );
                    }

                    /*
                     * --------------------------------------------------
                     * NESTED NORMALIZED FIELD
                     * --------------------------------------------------
                     *
                     * Example:
                     *
                     * source.service
                     * occurrence.severity
                     * context.requestId
                     */
                    else if (targetKey.contains(".")) {

                        String[] parts =
                                targetKey.split(
                                        "\\.",
                                        2
                                );

                        event.putField(
                                parts[0],
                                parts[1],
                                value
                        );
                    }

                    /*
                     * --------------------------------------------------
                     * NON-NESTED FIELD
                     * --------------------------------------------------
                     *
                     * Fields without a namespace are stored
                     * inside metadata.
                     */
                    else {

                        event.putField(
                                "metadata",
                                targetKey,
                                value
                        );
                    }
                }

                normalizedEvents.add(event);
            }

        } catch (Exception e) {

            System.err.println(
                    "Error while normalizing logs: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }

        return normalizedEvents;
    }

    /*
     * --------------------------------------------------
     * CONVERT JSON VALUES
     * --------------------------------------------------
     *
     * Converts JSONObject / JSONArray recursively into
     * normal Java Map / List structures.
     */
    private Object convertJsonValue(Object value) {

        if (value instanceof JSONObject jsonObject) {

            Map<String, Object> map =
                    new HashMap<>();

            for (String key :
                    jsonObject.keySet()) {

                map.put(
                        key,
                        convertJsonValue(
                                jsonObject.get(key)
                        )
                );
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
                                jsonArray.get(i)
                        )
                );
            }

            return list;
        }

        return value;
    }
}
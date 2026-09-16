package dev.rohit.buglens.BLR.Bundles.SpringBootBundler;

import dev.rohit.buglens.BLR.LogParser;
import dev.rohit.buglens.BLR.Bundles.SpringBootBundler.Configurations.FieldPattern;
import dev.rohit.buglens.BLR.Bundles.SpringBootBundler.Configurations.SpringBootConfig;

import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpringBootParser implements LogParser {

    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("(\\w+)=([^\\s]+)");

    private static final DateTimeFormatter DEFAULT_TIMESTAMP_FORMAT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private static final ZoneId SOURCE_ZONE = ZoneId.of("Asia/Kolkata");

    private final SpringBootLogTokenizer tokenizer;
    private final SpringBootConfigDetector configDetector;

    public SpringBootParser() {
        this.tokenizer = new SpringBootLogTokenizer();
        this.configDetector = new SpringBootConfigDetector();
    }

    @Override
    public JSONObject parse(String logLine) {

        JSONObject parsedData = new JSONObject();

        if (logLine == null || logLine.isBlank()) {
            return parsedData;
        }

        // ==========================================
        // TOKENIZE
        // ==========================================

        List<String> tokens = tokenizer.tokenize(logLine);

        // ==========================================
        // DETECT CONFIGURATION
        // ==========================================

        var configurations = configDetector.detect(logLine);

        if (configurations.isEmpty()) {
            return parsedData;
        }

        SpringBootConfig config = configurations.entrySet()
                .stream()
                .findFirst()
                .get()
                .getKey();

        double score = configurations.entrySet()
                .stream()
                .findFirst()
                .get()
                .getValue();

        if (score <= 0.0) {
            return parsedData;
        }

        // ==========================================
        // EXTRACT FIELDS
        // ==========================================

        String message = null;

        for (FieldPattern field : config.getFieldPatterns()) {

            int index = field.position() - 1;

            if (index >= tokens.size()) {
                continue;
            }

            // ======================================
            // REMAINDER FIELD
            // ======================================

            if (field.remainder()) {

                message = String.join(
                        " ",
                        tokens.subList(index, tokens.size()));

                continue;
            }

            String value = tokens.get(index);

            if (!field.pattern()
                    .matcher(value)
                    .matches()) {

                continue;
            }

            parsedData.put(
                    field.fieldName(),
                    convertValue(
                            field.fieldName(),
                            value));
        }

        // ==========================================
        // MESSAGE + KEY/VALUE EXTRACTION
        // ==========================================

        if (message != null) {

            Matcher keyValueMatcher = KEY_VALUE_PATTERN.matcher(message);

            while (keyValueMatcher.find()) {

                String key = keyValueMatcher.group(1);
                String value = keyValueMatcher.group(2);

                parsedData.put(key, value);
            }

            String cleanMessage = message
                    .replaceAll(
                            "\\s+\\w+=[^\\s]+",
                            "")
                    .trim();

            parsedData.put(
                    "message",
                    cleanMessage);
        }

        return parsedData;
    }

    private Object convertValue(
            String fieldName,
            String value) {

        if ("timestamp".equals(fieldName)) {
            return parseTimestamp(value);
        }

        if ("pid".equals(fieldName)) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ignored) {
                return value;
            }
        }

        return value;
    }

    private Instant parseTimestamp(String value) {

        // ISO timestamp with offset
        if (value.contains("T")
                && (value.contains("+")
                        || value.matches(".*-\\d{2}:\\d{2}$"))) {

            return OffsetDateTime
                    .parse(value)
                    .toInstant();
        }

        // Default Spring Boot timestamp
        LocalDateTime localDateTime = LocalDateTime.parse(
                value,
                DEFAULT_TIMESTAMP_FORMAT);

        return localDateTime
                .atZone(SOURCE_ZONE)
                .toInstant();
    }

    public void printParsedData(JSONObject parsedData) {
        if (parsedData == null || parsedData.isEmpty()) {
            System.out.println("No parsed data.");
            return;
        }

        System.out.println("===== PARSED DATA =====");
        System.out.println(parsedData.toString(4));
    }
}
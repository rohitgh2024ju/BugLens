package dev.rohit.buglens.ParserEngine.plugins;

import dev.rohit.buglens.ParserEngine.LogParser;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpringBootParser implements LogParser {

        private static final Pattern LOG_PATTERN = Pattern.compile(
                        "^(?<date>\\d{4}-\\d{2}-\\d{2})\\s+" +
                                        "(?<time>\\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\s+" +
                                        "(?<severity>INFO|WARN|ERROR|DEBUG|TRACE|FATAL)\\s+" +
                                        "\\[(?<thread>[^\\]]+)\\]\\s+" +
                                        "(?<logger>[\\w.$]+)\\s+-\\s+" +
                                        "(?<content>.*)$");

        private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("(\\w+)=([^\\s]+)");

        private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter
                        .ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        private static final ZoneId SOURCE_ZONE = ZoneId.of("Asia/Kolkata");

        @Override
        public JSONObject parse(String logLine) {

                JSONObject parsedData = new JSONObject();

                if (logLine == null || logLine.isBlank()) {
                        return parsedData;
                }

                Matcher matcher = LOG_PATTERN.matcher(logLine);

                if (!matcher.matches()) {
                        return parsedData;
                }

                // Parse timestamp from the Spring Boot log
                String timestampValue = matcher.group("date") + " " + matcher.group("time");

                LocalDateTime localDateTime = LocalDateTime.parse(
                                timestampValue,
                                TIMESTAMP_FORMATTER);

                // Convert IST → UTC Instant
                Instant timestamp = localDateTime
                                .atZone(SOURCE_ZONE)
                                .toInstant();

                parsedData.put("timestamp", timestamp);

                parsedData.put(
                                "severity",
                                matcher.group("severity"));

                parsedData.put(
                                "thread",
                                matcher.group("thread"));

                parsedData.put(
                                "logger",
                                matcher.group("logger"));

                String content = matcher.group("content");

                Matcher keyValueMatcher = KEY_VALUE_PATTERN.matcher(content);

                while (keyValueMatcher.find()) {

                        String key = keyValueMatcher.group(1);
                        String value = keyValueMatcher.group(2);

                        parsedData.put(key, value);
                }

                String message = content
                                .replaceAll("\\s+\\w+=[^\\s]+", "")
                                .trim();

                parsedData.put("message", message);

                return parsedData;
        }
}
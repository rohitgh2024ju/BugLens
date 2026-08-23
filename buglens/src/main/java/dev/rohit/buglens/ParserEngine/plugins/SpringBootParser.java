package dev.rohit.buglens.ParserEngine.plugins;

import dev.rohit.buglens.ParserEngine.LogParser;
import org.json.JSONObject;

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

        parsedData.put(
                "timestamp",
                matcher.group("date") + " " + matcher.group("time"));

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
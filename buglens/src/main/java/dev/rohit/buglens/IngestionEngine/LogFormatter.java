package dev.rohit.buglens.IngestionEngine;

import java.util.regex.Pattern;

public class LogFormatter {

    private final StructuralRegex regex = new StructuralRegexImpl();

    private final Pattern timeStampPattern = Pattern.compile(regex.TimeStampMatcher());
    private final Pattern datePattern = Pattern.compile(regex.DateMatcher());
    private final Pattern timePattern = Pattern.compile(regex.TimeMatcher());
    private final Pattern urlPattern = Pattern.compile(regex.URLMatcher());
    private final Pattern uuidPattern = Pattern.compile(regex.UUIDMatcher());
    private final Pattern ipPattern = Pattern.compile(regex.IPMatcher());
    private final Pattern severityPattern = Pattern.compile(regex.SeverityMatcher());
    private final Pattern keyValuePattern = Pattern.compile(regex.KeyValueMatcher());
    private final Pattern quotedPattern = Pattern.compile(regex.QuotedMatcher());
    private final Pattern bracketedPattern = Pattern.compile(regex.BracketedMatcher());
    private final Pattern classPathPattern = Pattern.compile(regex.ClassPathMatcher());
    private final Pattern separatorsPattern = Pattern.compile(regex.SeparatorsMatcher());
    private final Pattern argsPattern = Pattern.compile(regex.ArgsMatcher());

    public String format(String logLine) {
        if (logLine == null || logLine.isEmpty()) {
            return logLine;
        }

        // 1. Full Timestamps, URLs, UUIDs, IPs FIRST
        logLine = timeStampPattern.matcher(logLine).replaceAll("{TIMESTAMP}");
        logLine = urlPattern.matcher(logLine).replaceAll("{URL}");
        logLine = uuidPattern.matcher(logLine).replaceAll("{UUID}");
        logLine = ipPattern.matcher(logLine).replaceAll("{IP_ADDRESS}");

        // 2. Standalone Dates and Times
        logLine = datePattern.matcher(logLine).replaceAll("{DATE}");
        logLine = timePattern.matcher(logLine).replaceAll("{TIME}");

        // 3. Metadata & Encapsulated Content
        logLine = severityPattern.matcher(logLine).replaceAll("{SEVERITY}");
        logLine = keyValuePattern.matcher(logLine).replaceAll("{KEY_VALUE}");
        logLine = quotedPattern.matcher(logLine).replaceAll("{QUOTATION}");
        logLine = bracketedPattern.matcher(logLine).replaceAll("{BRACKETED_TEXT}");

        // 4. Dot-separated Java Classes / Packages
        logLine = classPathPattern.matcher(logLine).replaceAll("{CLASS}");

        // 5. Delimiters
        logLine = separatorsPattern.matcher(logLine).replaceAll("{SEPARATOR}");

        // 6. Catch-All Remaining Words LAST
        logLine = argsPattern.matcher(logLine).replaceAll("{ARGS}");

        return combineArgsToMessage(logLine);
    }

    private String combineArgsToMessage(String formattedLog) {
        // Matches from the FIRST {ARGS} all the way to the LAST {ARGS}
        return formattedLog.replaceAll("\\{ARGS\\}.*\\{ARGS\\}", "{MESSAGE}")
                .replaceAll("\\{ARGS\\}", "{MESSAGE}"); // Handles edge case of a single isolated {ARGS}
    }
}
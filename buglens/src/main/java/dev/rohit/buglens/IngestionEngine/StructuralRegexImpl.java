package dev.rohit.buglens.IngestionEngine;

public class StructuralRegexImpl implements StructuralRegex {

    @Override
    public String DateMatcher() {
        // Matches standard dates: YYYY-MM-DD, DD/MM/YYYY, Mon DD, etc.
        return "(?:\\b(?:\\d{4}[-/.]\\d{1,2}[-/.]\\d{1,2}|\\d{1,2}[-/.]\\d{1,2}[-/.]\\d{4}|(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*\\s+\\d{1,2})\\b)";
    }

    @Override
    public String TimeMatcher() {
        // Matches HH:MM:SS, HH:MM:SS.mmm, and 12-hour formats with AM/PM
        return "(?:\\b(?:[01]?\\d|2[0-3]):[0-5]\\d(?::[0-5]\\d(?:[.,]\\d{1,9})?)?(?:\\s?[AP]M)?\\b)";
    }

    @Override
    public String TimeStampMatcher() {
        // Matches YYYY-MM-DD HH:MM:SS.SSS, ISO-8601, and RFC-3339 formats
        return "(?:\\b\\d{4}-\\d{2}-\\d{2}[T ]\\d{2}:\\d{2}:\\d{2}(?:[.,]\\d{1,9})?(?:Z|[+-]\\d{2}:?\\d{2})?\\b)";
    }

    @Override
    public String SeverityMatcher() {
        // Matches standard log levels (case-insensitive)
        return "(?i)\\b(TRACE|DEBUG|INFO|WARN|WARNING|ERROR|FATAL|CRITICAL|SEVERE)\\b";
    }

    @Override
    public String KeyValueMatcher() {
        // Matches key-value pairs like foo=bar, user="john doe", or status:'active'
        return "(?:\\b[a-zA-Z_][a-zA-Z0-9_.-]*\\s*=\\s*(?:\"[^\"]*\"|'[^']*'|\\S+))";
    }

    @Override
    public String UUIDMatcher() {
        // Matches standard v1-v5 128-bit UUIDs
        return "(?:\\b[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}\\b)";
    }

    @Override
    public String IPMatcher() {
        // Matches IPv4 addresses and standard IPv6 formats
        return "(?:\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b|\\b(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}\\b)";
    }

    @Override
    public String URLMatcher() {
        // Matches http, https, and ftp URLs
        return "(?:https?|ftp)://[^\\s/$.?#].[^\\s]*";
    }

    @Override
    public String BracketedMatcher() {
        // Strictly matches content inside [...], <...>, or {...} (excluding already
        // created tokens)
        return "(?:(?<!\\{)\\[[^\\]]+\\]|<[^>]+>|\\{(?![A-Z_]+})[^\\}]+\\})";
    }

    @Override
    public String QuotedMatcher() {
        // Matches text inside single or double quotes, handling escaped quotes
        return "(?:\"(?:\\\\\"|[^\"])*\"|'(?:\\\\'|[^'])*')";
    }

    @Override
    public String ClassPathMatcher() {
        // Matches Java package paths/classes like
        // com.buglens.payment.PaymentApplication
        return "(?:\\b(?:[a-zA-Z_][a-zA-Z0-9_]*\\.)+[a-zA-Z_][a-zA-Z0-9_]*\\b)";
    }

    @Override
    public String SeparatorsMatcher() {
        // Matches common log delimiters like ->, =>, ::, |, -, :
        return "(?:->|=>|::|---|==|\\||-|:)";
    }

    @Override
    public String ArgsMatcher() {
        // Matches words/tokens but skips existing tokens like {TIMESTAMP} or {SEVERITY}
        return "(?<!\\{)\\b[a-zA-Z0-9_-]+\\b(?!\\})";
    }
}
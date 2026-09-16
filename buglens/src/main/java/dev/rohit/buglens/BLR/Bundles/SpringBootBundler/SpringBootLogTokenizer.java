package dev.rohit.buglens.BLR.Bundles.SpringBootBundler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpringBootLogTokenizer {

    private static final Pattern SPACE_TIMESTAMP = Pattern.compile(
            "\\d{4}-\\d{2}-\\d{2}\\s+" +
                    "\\d{2}:\\d{2}:\\d{2}\\.\\d{3}");

    public List<String> tokenize(String logLine) {

        List<String> tokens = new ArrayList<>();

        if (logLine == null || logLine.isBlank()) {
            return tokens;
        }

        StringBuilder current = new StringBuilder();

        boolean insideBracket = false;
        boolean insideQuote = false;

        Matcher timestampMatcher = SPACE_TIMESTAMP.matcher(logLine);

        int timestampStart = -1;
        int timestampEnd = -1;

        if (timestampMatcher.find()) {
            timestampStart = timestampMatcher.start();
            timestampEnd = timestampMatcher.end();
        }

        for (int i = 0; i < logLine.length(); i++) {

            // ==============================
            // SPACE-BASED TIMESTAMP
            // ==============================

            if (i == timestampStart) {

                if (!current.isEmpty()) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }

                tokens.add(
                        logLine.substring(timestampStart, timestampEnd));

                i = timestampEnd - 1;
                continue;
            }

            char ch = logLine.charAt(i);

            // ==============================
            // BRACKETS
            // ==============================

            if (ch == '[' && !insideQuote) {
                insideBracket = true;
                current.append(ch);
                continue;
            }

            if (ch == ']' && insideBracket && !insideQuote) {
                insideBracket = false;
                current.append(ch);
                continue;
            }

            // ==============================
            // QUOTES
            // ==============================

            if (ch == '"' && !insideBracket) {
                insideQuote = !insideQuote;
                current.append(ch);
                continue;
            }

            // ==============================
            // WHITESPACE
            // ==============================

            if (Character.isWhitespace(ch)
                    && !insideBracket
                    && !insideQuote) {

                if (!current.isEmpty()) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }

            } else {
                current.append(ch);
            }
        }

        if (!current.isEmpty()) {
            tokens.add(current.toString());
        }

        return tokens;
    }
}
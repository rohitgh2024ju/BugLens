package dev.rohit.buglens.IngestionEngine;

public interface StructuralRegex {
    public String DateMatcher(); // returns {DATE}

    public String TimeMatcher(); // returns {TIME}

    public String TimeStampMatcher(); // returns {TIMESTAMP}

    public String SeverityMatcher(); // returns {SEVERITY}

    public String KeyValueMatcher(); // returns {KEY-VALUE}

    public String UUIDMatcher(); // returns {UUID}

    public String IPMatcher(); // returns {IP_ADDRESS}

    public String URLMatcher(); // returns {URL}

    public String BracketedMatcher(); // returns {BRACKETED_TEXT}

    public String QuotedMatcher(); // returns {QUOTATION}

    public String ClassPathMatcher(); // returns {CLASS}

    public String SeparatorsMatcher(); // returns {(separators like -,|,:,::,=>)}

    public String ArgsMatcher(); // returns {ARGS}
}
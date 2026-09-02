package dev.rohit.buglens.CorrelationEngine.model;

public enum CorrelationType {
    REQUEST_ID,
    TRACE_ID,
    TRANSACTION_ID,
    THREAD,
    SAME_SERVICE,
    SAME_COMPONENT,
    TIME
}

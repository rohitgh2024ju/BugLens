package dev.rohit.buglens.CorrelationEngine;

import dev.rohit.buglens.CorrelationEngine.model.CorrelationGroup;


// entry point
public class CorrelationEngine {
    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
        CorrelationGroup correlationGroup = new CorrelationGroup();

        correlationGroup.viewCorelationByReqId();
        correlationGroup.viewCorelationByTraceId();
        correlationGroup.viewCorrelationByTime(2);
    }
}

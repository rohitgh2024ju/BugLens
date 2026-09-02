package dev.rohit.buglens.BLR.Bundles;

import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.rohit.buglens.CorrelationEngine.model.CorrelationResult;
import dev.rohit.buglens.CorrelationEngine.model.CorrelationType;
import dev.rohit.buglens.GraphEngine.model.EvidenceScore;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;

public interface CorrelationBundle {
    Set<CorrelationType> getSupportedCorrelationTypes();

    List<CorrelationResult> correlate(
        List<NormalizedEvent> events,
        CorrelationType type,
        long time
    );

    Map<CorrelationType, EvidenceScore> getEvidenceScores();

    List<CorrelationResult> groupByRequestId(List<NormalizedEvent> events);
    List<CorrelationResult> groupByTraceId(List<NormalizedEvent> events);
    List<CorrelationResult> groupByTransactionId(List<NormalizedEvent> events);
    List<CorrelationResult> groupByService(List<NormalizedEvent> events);
    List<CorrelationResult> groupByComponent(List<NormalizedEvent> events);
    List<CorrelationResult> groupByThread(List<NormalizedEvent> events);
    List<CorrelationResult> groupByTime(List<NormalizedEvent> events, long seconds);
}

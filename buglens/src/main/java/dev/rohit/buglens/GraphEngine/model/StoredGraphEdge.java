package dev.rohit.buglens.GraphEngine.model;

import java.util.Set;

import dev.rohit.buglens.CorrelationEngine.model.CorrelationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoredGraphEdge {
    private String sourceNodeId;

    private String targetNodeId;

    private Set<CorrelationType> evidenceTypes;

    private double strength;

    private double confidence;
}

package dev.rohit.buglens.GraphEngine.model;

import java.util.HashSet;
import java.util.Set;

import dev.rohit.buglens.CorrelationEngine.model.CorrelationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRelationship {

    @Builder.Default
    private Set<CorrelationType> evidenceTypes = new HashSet<>();

    private double strength;

    private double confidence;
}

// Which two events are related, and what evidence says they're related?
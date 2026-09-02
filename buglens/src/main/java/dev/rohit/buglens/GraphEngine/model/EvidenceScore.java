package dev.rohit.buglens.GraphEngine.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EvidenceScore {
    private double strengthWeight;
    private double reliabilityWeight;
}

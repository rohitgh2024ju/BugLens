package dev.rohit.buglens.AIAnalysisEngine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor 
@NoArgsConstructor 
public class AIAnalysis {
    private String incidentId;
    private Object analysis;
}

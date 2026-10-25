package dev.rohit.buglens.Application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor 
public class CachedAnalysisEntry {
    private long seconds;
    private double threshold;
    private boolean print;
    private AnalysisResponse response;
}

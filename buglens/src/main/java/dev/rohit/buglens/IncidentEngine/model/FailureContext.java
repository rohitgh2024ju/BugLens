package dev.rohit.buglens.IncidentEngine.model;

import java.util.Set;

import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class FailureContext {
    private NormalizedEvent failureEvent;
    private Set<NormalizedEvent> relatedEvents;
}

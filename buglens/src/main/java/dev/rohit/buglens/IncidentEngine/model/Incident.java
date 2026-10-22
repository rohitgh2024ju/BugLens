package dev.rohit.buglens.IncidentEngine.model;

import java.util.Set;

import dev.rohit.buglens.IncidentMetadata.IncidentMetadata;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Incident {
    
    private String id;
    private IncidentMetadata metadata;
    private Set<FailureContext> failureContexts;
}

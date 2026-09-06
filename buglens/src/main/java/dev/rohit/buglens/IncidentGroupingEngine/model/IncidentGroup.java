package dev.rohit.buglens.IncidentGroupingEngine.model;

import java.util.HashSet;
import java.util.Set;

import dev.rohit.buglens.IncidentEngine.model.Incident;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IncidentGroup {

    private String groupId;

    @Builder.Default
    private Set<Incident> incidents = new HashSet<>();
}
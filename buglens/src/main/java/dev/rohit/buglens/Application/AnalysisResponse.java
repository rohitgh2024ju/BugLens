package dev.rohit.buglens.Application;

import java.util.List;

import dev.rohit.buglens.Dashboard.DashboardResponse;
import dev.rohit.buglens.IncidentEngine.model.Incident;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class AnalysisResponse {

    private DashboardResponse dashboard;
    private List<Incident> incidents;
}

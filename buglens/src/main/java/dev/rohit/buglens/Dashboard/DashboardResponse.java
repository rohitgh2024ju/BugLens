package dev.rohit.buglens.Dashboard;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor 
public class DashboardResponse {
    private long totalEvents;
    private long detectedIncidents;
    private long affectedServices;
    private long errorEvents;
    private long criticalEvents;
    private long correlations;

    private List<SeverityBreakdown> severityBreakdown;
    private List<ServiceBreakdown> topAffectedServices;
}

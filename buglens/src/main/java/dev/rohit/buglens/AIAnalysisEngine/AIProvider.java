package dev.rohit.buglens.AIAnalysisEngine;

import dev.rohit.buglens.IncidentEngine.model.Incident;

public interface AIProvider {
    AIAnalysis analyzeIncident(Incident incident);
}

package dev.rohit.buglens.AIAnalysisEngine;

import org.springframework.stereotype.Service;

import dev.rohit.buglens.IncidentEngine.model.Incident;

@Service
public class AIAnalysisService {

    private final AIProvider aiProvider;

    public AIAnalysisService(AIProvider aiProvider) {
        this.aiProvider = aiProvider;
    }

    public AIAnalysis analyzeIncident(Incident incident) {

        if (incident == null) {
            throw new IllegalArgumentException(
                    "Incident cannot be null");
        }

        return aiProvider.analyzeIncident(incident);
    }
}
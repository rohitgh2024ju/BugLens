package dev.rohit.buglens.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.rohit.buglens.AIAnalysisEngine.AIAnalysis;
import dev.rohit.buglens.AIAnalysisEngine.AIAnalysisService;
import dev.rohit.buglens.IncidentEngine.model.Incident;

@RestController
@RequestMapping("/api/reasoning")
public class ReasoningController {
    private final AIAnalysisService aiAnalysisService;

    public ReasoningController(AIAnalysisService aiAnalysisService) {
        this.aiAnalysisService = aiAnalysisService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<AIAnalysis> analyze(
            @RequestBody Incident incident) {
        AIAnalysis analysis = aiAnalysisService.analyzeIncident(incident);

        return ResponseEntity.ok(analysis);
    }
}

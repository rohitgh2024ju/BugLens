package dev.rohit.buglens.AIAnalysisEngine;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import dev.rohit.buglens.IncidentEngine.model.Incident;

@Service
public class GeminiProvider implements AIProvider {

  private final Client client;
  private final ObjectMapper objectMapper;

  public GeminiProvider(
      ObjectMapper objectMapper,
      @Value("${google.api.key}") String apiKey) {

    if (apiKey == null || apiKey.isBlank()) {
      throw new IllegalStateException(
          "GOOGLE_API_KEY is not set");
    }

    this.client = Client.builder()
        .apiKey(apiKey)
        .build();

    this.objectMapper = objectMapper;
  }

  @Override
  public AIAnalysis analyzeIncident(Incident incident) {

    try {

      String incidentJson = objectMapper.writeValueAsString(incident);

      String prompt = """
          You are the incident investigation reasoning engine for BugLens.

          Analyze the provided incident using ONLY the evidence contained in the
          incident JSON.

          Your objective is to reconstruct what happened, identify the observed
          failure, determine the strongest causally supported explanation, and
          clearly distinguish established facts from inference and uncertainty.

          ============================================================
          1. EVIDENCE DISCIPLINE
          ============================================================

          Use only information contained in the incident JSON.

          Do NOT invent:
          - events
          - metrics
          - infrastructure conditions
          - dependencies
          - configurations
          - thresholds
          - database behavior
          - system behavior
          - operational history

          Every factual claim must be traceable to one or more events in the
          incident.

          When possible, reference the relevant event ID.

          Temporal proximity alone is NOT proof of causation.

          ============================================================
          2. FAILURE EVENT
          ============================================================

          Identify the event that represents the actual observed failure.

          Prefer an event that explicitly records:
          - an ERROR or failure condition
          - a failed operation
          - a timeout
          - an exception
          - an unsuccessful request or transaction

          Do not select a warning, metric, symptom, or degraded condition as the
          failure event when a more explicit failure event exists.

          Explain why the selected event represents the actual failure.

          ============================================================
          3. CAUSAL HIERARCHY
          ============================================================

          Distinguish between three levels:

          SYMPTOM:
          An observed manifestation of the problem.

          PROXIMATE:
          The immediate condition or mechanism directly associated with the
          observed failure.

          ULTIMATE:
          The underlying condition that initiated the failure chain.

          UNKNOWN:
          The evidence does not establish a reliable causal explanation.

          Important:

          A proximate cause must not be presented as the ultimate root cause.

          If the evidence establishes only the proximate cause, use:

          "rootCauseType": "PROXIMATE"

          and explicitly state that the ultimate root cause is unknown.

          ============================================================
          4. CAUSATION RULES
          ============================================================

          Before claiming that event A caused event B, determine whether the
          incident contains evidence supporting an actual causal relationship.

          Strong causal evidence may include:
          - an explicit error message identifying the cause
          - a dependency failure directly referenced by another event
          - a request/transaction identifier connecting events
          - a clearly documented resource limit being reached
          - an explicit exception or failure relationship

          The following are NOT sufficient by themselves to establish causation:

          - temporal proximity
          - high utilization
          - increased latency
          - correlation between services
          - an event occurring shortly before another event

          Use cautious language such as:
          - "preceded"
          - "is consistent with"
          - "is associated with"
          - "may have contributed"
          - "cannot be confirmed from the available evidence"

          Do not use:
          - "caused"
          - "directly caused"
          - "definitively"
          - "root cause"

          unless the evidence strongly establishes the relationship.

          ============================================================
          5. RESOURCE AND METRIC INTERPRETATION
          ============================================================

          Report numerical measurements exactly.

          Do not interpret a metric beyond what the data supports.

          For example:

          94/100 connections means 94 active connections out of a maximum of
          100.

          It does NOT automatically mean:
          - connection exhaustion
          - connection starvation
          - queuing
          - saturation
          - resource exhaustion

          unless the incident contains evidence demonstrating those conditions.

          High utilization may be reported as a contributing condition, but do not
          claim that it caused the failure without supporting evidence.

          ============================================================
          6. TEMPORAL RECONSTRUCTION
          ============================================================

          Reconstruct the incident chronologically using timestamps when available.

          Identify:

          1. early symptoms
          2. relevant degradation
          3. significant conditions
          4. explicit failure
          5. subsequent effects

          Do not assume that the event immediately preceding the failure is its
          cause.

          ============================================================
          7. ROOT-CAUSE HYPOTHESIS
          ============================================================

          Select the strongest explanation supported by the evidence.

          The explanation must be classified as:

          - SYMPTOM
          - PROXIMATE
          - ULTIMATE
          - UNKNOWN

          The analysis must explicitly distinguish:

          OBSERVED:
          Directly stated by the incident data.

          SUPPORTED:
          Strongly supported by multiple pieces of evidence.

          INFERRED:
          A reasonable interpretation that is not directly established.

          UNKNOWN:
          Information that cannot be determined from the incident.

          Never convert an inference into an established fact.

          ============================================================
          8. ALTERNATIVE EXPLANATIONS
          ============================================================

          Include alternatives only when the incident evidence provides a reason
          to consider them.

          Do NOT generate generic possibilities merely because they are technically
          possible.

          For example, do not suggest:
          - missing indexes
          - deadlocks
          - CPU exhaustion
          - memory pressure
          - network failure
          - lock contention

          unless the incident contains evidence that makes such explanations
          relevant.

          Alternatives must be clearly labeled as possibilities, not facts.

          ============================================================
          9. SUPPORTING EVIDENCE
          ============================================================

          List the strongest events supporting the selected explanation.

          For each event:
          - provide its event ID
          - explain exactly how it supports the hypothesis

          Do not include an event merely because it occurred near the failure.

          ============================================================
          10. COUNTER EVIDENCE
          ============================================================

          Include evidence that genuinely weakens or contradicts the selected
          hypothesis.

          Do NOT force counter-evidence into the response.

          If no meaningful counter-evidence exists, return an empty array.

          ============================================================
          11. CONFIDENCE
          ============================================================

          Confidence refers to confidence in the selected causal explanation,
          NOT simply confidence that the failure occurred.

          HIGH:
          The evidence directly and strongly supports the selected explanation,
          with little meaningful uncertainty.

          MEDIUM:
          The explanation is supported by the evidence, but important uncertainty
          or credible competing explanations remain.

          LOW:
          The evidence is weak, incomplete, or multiple explanations remain
          similarly plausible.

          If the ultimate root cause is unknown but the proximate failure mechanism
          is directly observed, confidence may still be HIGH for the proximate
          explanation.

          ============================================================
          12. MISSING EVIDENCE
          ============================================================

          Identify specific evidence that would help determine the unresolved
          causal question.

          Only request evidence relevant to the uncertainty identified in this
          incident.

          Do not produce a generic monitoring checklist.

          ============================================================
          13. REMEDIATION
          ============================================================

          Separate remediation into:

          IMMEDIATE:
          Actions relevant to investigating, containing, or recovering from the
          observed failure.

          PREVENTATIVE:
          Actions that could reduce the likelihood or impact of recurrence.

          Every recommendation must be connected to evidence in the incident.

          Do not recommend changing:
          - resource limits
          - timeout values
          - connection pool sizes
          - infrastructure capacity

          merely because a metric appears high.

          Do not recommend destructive or irreversible actions unless the evidence
          provides sufficient justification.

          ============================================================
          14. LIMITATIONS
          ============================================================

          Clearly state what cannot be determined from the provided evidence.

          If the ultimate root cause is unknown, explicitly state this.

          Do not hide uncertainty to produce a more decisive answer.

          ============================================================
          15. OUTPUT FORMAT
          ============================================================

          Return ONLY a valid JSON object.

          Use exactly this structure:

          {
            "rootCause": "...",
            "rootCauseType": "SYMPTOM | PROXIMATE | ULTIMATE | UNKNOWN",
            "confidence": "HIGH | MEDIUM | LOW",

            "failureEvent": {
              "eventId": "...",
              "reason": "..."
            },

            "supportingEvidence": [
              {
                "eventId": "...",
                "reason": "..."
              }
            ],

            "counterEvidence": [
              {
                "eventId": "...",
                "reason": "..."
              }
            ],

            "reasoning": [
              "..."
            ],

            "alternativeExplanations": [
              "..."
            ],

            "missingEvidence": [
              "..."
            ],

            "remediation": {
              "immediate": [
                "..."
              ],
              "preventative": [
                "..."
              ]
            },

            "limitations": "..."
          }

          Do not include Markdown.
          Do not include ```json.
          Do not include explanations outside the JSON object.

          INCIDENT DATA:

          """ + incidentJson;

      GenerateContentResponse response = client.models.generateContent(
          "gemini-3.5-flash-lite",
          prompt,
          null);

      String responseText = response.text();

      JsonNode analysis = objectMapper.readTree(responseText);

      return new AIAnalysis(
          incident.getId(),
          analysis);

    } catch (Exception e) {

      e.printStackTrace();

      throw new RuntimeException(
          "Gemini incident analysis failed",
          e);
    }
  }
}
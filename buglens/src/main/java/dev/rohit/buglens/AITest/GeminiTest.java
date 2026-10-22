package dev.rohit.buglens.AITest;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

public class GeminiTest {

   public static void main(String[] args) {

      String apiKey = System.getenv("GOOGLE_API_KEY");

      if (apiKey == null || apiKey.isBlank()) {
         System.out.println("GOOGLE_API_KEY is not set.");
         return;
      }

      Client client = Client.builder()
            .apiKey(apiKey)
            .build();

      String incidentData = """
            Incident ID: INC-001

            Failure Event:
            PaymentService - ERROR
            "Database connection timeout after 30 seconds"

            Related Events:

            Database - INFO
            "Active connections: 98/100"

            PaymentService - WARN
            "Connection pool usage reached 95%"

            PaymentService - ERROR
            "Failed to acquire database connection"

            PaymentService - ERROR
            "Request processing failed"
            """;

      String prompt = """
            You are an incident investigation assistant for BugLens,
            an observability and incident analysis platform.

            Analyze the incident data provided below.

            Your goal is to determine the most probable root cause
            using ONLY the information contained in the incident data.

            IMPORTANT RULES:

            1. Do not invent facts, events, metrics, configurations,
               dependencies, or system behavior that are not present
               in the provided data.

            2. Clearly distinguish:
               - Direct evidence: what an event explicitly reports.
               - Inference: what can reasonably be concluded from
                 multiple events.

            3. Do not treat correlation as proof of causation.
               Explain why the available evidence supports the
               proposed root cause.

            4. Identify the failure event that represents the
               primary failure.

            5. Identify the events that provide the strongest
               evidence for the root-cause hypothesis.

            6. Use timestamps and event relationships when available
               to establish the sequence of events.

            7. If the evidence is insufficient to determine a root
               cause, explicitly say so instead of guessing.

            8. Remediation recommendations must be relevant to the
               identified failure and must not assume infrastructure
               details that are not supported by the evidence.

            9. Keep the analysis technically precise and concise.

            Return the analysis using exactly this structure:

            INCIDENT
            <incident id>

            ROOT CAUSE
            <one-sentence description of the most probable root cause>

            CONFIDENCE
            <High / Medium / Low>

            FAILURE EVENT
            <Event ID and short explanation>

            SUPPORTING EVIDENCE
            - <Event ID>: <what this event tells us>
            - <Event ID>: <what this event tells us>
            - <Event ID>: <what this event tells us>

            REASONING
            1. <first logical observation>
            2. <second logical observation>
            3. <third logical observation>
            4. <how these observations lead to the root-cause hypothesis>

            REMEDIATION
            Immediate:
            - <action>
            - <action>

            Preventative:
            - <action>
            - <action>

            LIMITATIONS
            <Briefly state any important uncertainty or missing evidence.
            If there are no significant limitations, say "None.">

            INCIDENT DATA:
            """
            + incidentData;

      try {

         GenerateContentResponse response = client.models.generateContent(
               "gemini-3.8-flash",
               prompt,
               null);

         System.out.println("\n========== GEMINI INCIDENT ANALYSIS ==========\n");
         System.out.println(response.text());
         System.out.println("\n==============================================");

      } catch (Exception e) {

         System.out.println("Gemini API request failed:");
         e.printStackTrace();

      }
   }
}
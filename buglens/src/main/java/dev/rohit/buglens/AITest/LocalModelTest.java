package dev.rohit.buglens.AITest;

import dev.langchain4j.model.ollama.OllamaChatModel;

import java.time.Duration;

public class LocalModelTest {

  public static void main(String[] args) {

    System.out.println("Connecting to Ollama...");

    OllamaChatModel model = OllamaChatModel.builder()
        .baseUrl("http://localhost:11434")
        .modelName("qwen2.5:0.5b") // 0.5B model fits easily in t3.medium memory
        .temperature(0.1)
        .timeout(Duration.ofSeconds(15))
        .format("json")
        .build();

    String systemPrompt = """
        You are BugLens Incident Metadata Engine.
        Synthesize incident data into high-level metadata.
        Return ONLY valid JSON matching schema:
        {
          "title": "string",
          "description": "string",
          "service": "string",
          "exceptionType": "string",
          "tags": ["string"]
        }
        """;

    String incidentPayload = """
                {
            "id": "INC-002",
            "failureContexts": [
                {
                    "failureEvent": {
                        "id": "EVT-4B72A91C-6D83-42F1-A8E7-91C53D20B644",
                        "timestamp": "2026-10-04T08:17:23Z",
                        "source": {
                            "service": "inventory-service",
                            "host": "inventory-server-02",
                        },
                        "occurrence": {
                            "exception": "HikariPool-1 - Connection is not available",
                            "level": "ERROR",
                        },
                        "context": {
                            "message": "Timeout after 30000ms waiting for a database connection"
                        },
                        "metadata": {"requestId": "req-019", "database": "inventory-db"},
                    },
                    "relatedEvents": [
                        {
                            "id": "EVT-93C18F52-71A4-4B29-B6D3-5E827F10A921",
                            "timestamp": "2026-10-04T08:17:10Z",
                            "source": {"service": "inventory-service"},
                            "occurrence": {"exception": "SQLTimeoutException", "level": "WARN"},
                            "context": {
                                "message": "Database query exceeded configured timeout"
                            },
                            "metadata": {"requestId": "req-019"},
                        },
                        {
                            "id": "EVT-2A64D9E7-35F1-48C2-91B6-7E04A83F512C",
                            "timestamp": "2026-10-04T08:17:25Z",
                            "source": {"service": "inventory-service"},
                            "occurrence": {
                                "exception": "InventoryFetchException",
                                "level": "ERROR",
                            },
                            "context": {"message": "Unable to retrieve inventory data"},
                            "metadata": {"requestId": "req-019"},
                        },
                    ],
                }
            ],
        }
        """;

    String fullPrompt = systemPrompt + "\nIncident Payload:\n" + incidentPayload;

    System.out.println("Processing local LLM request...");
    long startTime = System.currentTimeMillis();

    String jsonResponse = model.generate(fullPrompt);

    long endTime = System.currentTimeMillis();

    System.out.println("\nExecution Time: " + (endTime - startTime) + " ms");
    System.out.println("Model Output:");
    System.out.println(jsonResponse);
  }
}
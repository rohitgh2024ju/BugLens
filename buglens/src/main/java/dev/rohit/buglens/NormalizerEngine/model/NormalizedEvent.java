package dev.rohit.buglens.NormalizerEngine.model;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class NormalizedEvent {
    private String id;
    private String timestamp;

    @Builder.Default
    private Map<String, Object> source = new HashMap<>();
    @Builder.Default
    private Map<String, Object> occurrence = new HashMap<>();
    @Builder.Default
    private Map<String, Object> context = new HashMap<>();
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();

    public void putField(String category, String key, Object value) {
        switch (category.toLowerCase()) {
            case "source" -> source.put(key, value);
            case "occurrence" -> occurrence.put(key, value);
            case "context" -> context.put(key, value);
            case "metadata" -> metadata.put(key, value);
            default -> metadata.put(category + "." + key, value);
        }
    }
}

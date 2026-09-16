package dev.rohit.buglens.BLR.Bundles.SpringBootBundler;

import dev.rohit.buglens.BLR.Bundles.SpringBootBundler.Configurations.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpringBootConfigDetector {

    private final List<SpringBootConfig> configurations;

    public SpringBootConfigDetector() {
        this.configurations = List.of(
                new SpringBootDefaultConfig(),
                new SpringBootPidConfig());
    }

    public Map<SpringBootConfig, Double> detect(String logLine) {

        SpringBootLogTokenizer tokenizer = new SpringBootLogTokenizer();
        List<String> tokens = tokenizer.tokenize(logLine);

        Map<SpringBootConfig, Double> configResults = new HashMap<>();

        for (SpringBootConfig config : this.configurations) {
            double score = calculateScore(tokens, config);
            configResults.put(config, score);
        }

        return configResults.entrySet()
                .stream()
                .sorted(Map.Entry.<SpringBootConfig, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

    private double calculateScore(
            List<String> tokens, SpringBootConfig config) {
        double totalWeight = 0.0;
        double matchedWeight = 0.0;

        for (FieldPattern field : config.getFieldPatterns()) {
            totalWeight += field.weight();
            int index = field.position() - 1;

            if (index >= tokens.size()) {
                if (field.required()) {
                    return 0.0;
                }
                continue;
            }

            String token = tokens.get(index);

            if (field.pattern().matcher(token).matches()) {
                matchedWeight += field.weight();
            } else if (field.required()) {
                return 0.0;
            }
        }

        // Structural patterns
        for (StructurePattern structure : config.getStructurePatterns()) {

            totalWeight += structure.weight();
            int index = structure.position() - 1;

            if (index >= tokens.size()) {
                return 0.0;
            }
            String token = tokens.get(index);

            if (structure.pattern().matcher(token).matches()) {
                matchedWeight += structure.weight();
            } else {
                return 0.0;
            }
        }

        if (totalWeight == 0) {
            return 0.0;
        }

        return (matchedWeight / totalWeight) * 100.0;
    }
}
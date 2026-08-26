package dev.rohit.buglens.NormalizerEngine;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import dev.rohit.buglens.IngestionEngine.format.FormatDetector;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;

public class NormalizerEngine {

    public void runEngine() {
        try {
            FormatDetector formatDetector = new FormatDetector();
            String parserClass = formatDetector.detect();

            Normalizer normalizer = new Normalizer(parserClass, "buglens/logs/output.jsonl");
            List<NormalizedEvent> eventList = normalizer.normalize();

            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            File outputFile = new File("buglens/logs/normalized-events.jsonl");

            if (outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }

            mapper.writeValue(outputFile, eventList);

            System.out.println(" Successfully saved " + eventList.size()
                    + " normalized events to: " + outputFile.getAbsolutePath());

        } catch (Exception e) {
            System.err.println("Failed to export normalized events to JSON file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        NormalizerEngine normalizerEngine = new NormalizerEngine();

        normalizerEngine.runEngine();
    }
}

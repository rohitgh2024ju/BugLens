package dev.rohit.buglens.NormalizerEngine;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import dev.rohit.buglens.IngestionEngine.format.FormatDetector;
import dev.rohit.buglens.IngestionEngine.format.LogFormat;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;
import dev.rohit.buglens.QueryLayer.config.NitriteMultiTenantConfig;
import dev.rohit.buglens.QueryLayer.repository.EventRepository;

public class NormalizerEngine {

    public void runEngine(String clientId) {
        try {
            FormatDetector formatDetector =
                    new FormatDetector();

            LogFormat format =
                    formatDetector.detect(clientId);

            Path inputPath =
                    Paths.get(
                            "buglens",
                            "logs",
                            "output-" + clientId + ".jsonl");

            Normalizer normalizer = new Normalizer(format.getParser(), inputPath);
            List<NormalizedEvent> eventList = normalizer.normalize();

            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            // mapper.writeValue(outputFile, eventList);

            EventRepository eventRepository = new EventRepository();
            eventRepository.saveAll(clientId, eventList);

            System.out.println(" Successfully saved " + eventList.size() + " events.");

            System.out.println("Checking immediately:");

            eventRepository.viewAll(clientId);

            NitriteMultiTenantConfig.closeAll();

        } catch (Exception e) {
            System.err.println("Failed to export normalized events to Nitrite Database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        NormalizerEngine normalizerEngine = new NormalizerEngine();

        normalizerEngine.runEngine("000");
    }
}

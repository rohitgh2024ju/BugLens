package dev.rohit.buglens.NormalizerEngine;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import dev.rohit.buglens.IngestionEngine.format.FormatDetector;
import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;
import dev.rohit.buglens.QueryLayer.config.NitriteMultiTenantConfig;
import dev.rohit.buglens.QueryLayer.repository.EventRepository;

public class NormalizerEngine {

    public void runEngine() {
        try {
            FormatDetector formatDetector = new FormatDetector();
            String parserClass = formatDetector.detect();

            Normalizer normalizer = new Normalizer(parserClass, "buglens/logs/output.jsonl");
            List<NormalizedEvent> eventList = normalizer.normalize();

            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            // mapper.writeValue(outputFile, eventList);

            EventRepository eventRepository = new EventRepository();
            eventRepository.saveAll("000", eventList);

            System.out.println(" Successfully saved " + eventList.size() + " events.");

            System.out.println("Checking immediately:");

            eventRepository.viewAll("000");

            NitriteMultiTenantConfig.closeAll();

        } catch (Exception e) {
            System.err.println("Failed to export normalized events to Nitrite Database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        NormalizerEngine normalizerEngine = new NormalizerEngine();

        normalizerEngine.runEngine();
    }
}

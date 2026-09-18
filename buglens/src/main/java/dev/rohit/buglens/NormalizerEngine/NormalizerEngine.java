package dev.rohit.buglens.NormalizerEngine;

import java.nio.file.Path;
import java.util.List;

import dev.rohit.buglens.Configpaths.BugLensPaths;
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

            System.out.println(
                    "DETECTED FORMAT: "
                            + format.getName()
            );

            System.out.println(
                    "FORMAT ID: "
                            + format.getParser()
            );

            System.out.println(
                    "SELECTED PARSER: "
                            + format.getParser()
            );

            System.out.println(
                    "CONFIDENCE: "
                            + format.getConfidence()
            );

            Path inputPath =
                    BugLensPaths.LOGS_DIR.resolve(
                            "output-" + clientId + ".jsonl"
                    );

            Normalizer normalizer =
                    new Normalizer(
                            format,
                            inputPath
                    );

            List<NormalizedEvent> eventList =
                    normalizer.normalize();

            EventRepository eventRepository =
                    new EventRepository();

            eventRepository.saveAll(
                    clientId,
                    eventList
            );

            System.out.println(
                    "Successfully saved "
                            + eventList.size()
                            + " events."
            );

            System.out.println(
                    "Checking immediately:"
            );

            eventRepository.viewAll(clientId);

            NitriteMultiTenantConfig.closeAll();

        } catch (Exception e) {

            System.err.println(
                    "Failed to normalize and save events: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        NormalizerEngine normalizerEngine =
                new NormalizerEngine();

        normalizerEngine.runEngine("000");
    }
}
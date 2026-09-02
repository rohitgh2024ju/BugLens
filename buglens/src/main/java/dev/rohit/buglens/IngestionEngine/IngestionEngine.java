package dev.rohit.buglens.IngestionEngine;

import dev.rohit.buglens.IngestionEngine.context.ProcessingContext;
import dev.rohit.buglens.IngestionEngine.format.FormatDetector;

public class IngestionEngine {
    public static void main(String[] args) {
        FormatDetector formatDetector = new FormatDetector();
        String parser = formatDetector.detect();

        ProcessingContext processingContext = new ProcessingContext();
        processingContext.setLogFormat(parser);
        
        System.out.println(parser);
        formatDetector.getDetectionDetails();
    }
}
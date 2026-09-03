package dev.rohit.buglens.IngestionEngine;

import dev.rohit.buglens.IngestionEngine.context.ProcessingContext;
import dev.rohit.buglens.IngestionEngine.format.FormatDetector;
import dev.rohit.buglens.IngestionEngine.format.LogFormat;

public class IngestionEngine {
    public static void main(String[] args) {
        FormatDetector formatDetector = new FormatDetector();
        LogFormat format = formatDetector.detect();

        ProcessingContext processingContext = new ProcessingContext();
        processingContext.setLogFormat(format);
        
        System.out.println(format.getParser());
        formatDetector.getDetectionDetails();
    }
}
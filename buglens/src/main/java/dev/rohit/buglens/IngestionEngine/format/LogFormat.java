package dev.rohit.buglens.IngestionEngine.format;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogFormat {
    private String fileId;
    private String name;
    private String parser;
    private double confidence;
}

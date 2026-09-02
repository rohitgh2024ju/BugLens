package dev.rohit.buglens.IngestionEngine.context;


public class ProcessingContext {
    private String logFormat;

    public ProcessingContext() {}

    public String getLogFormat() {
        return logFormat;
    }

    public void setLogFormat(String format) {
        this.logFormat = format;
    }
}

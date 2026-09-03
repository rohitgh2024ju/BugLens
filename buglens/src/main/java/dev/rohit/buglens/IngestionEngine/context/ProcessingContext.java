package dev.rohit.buglens.IngestionEngine.context;

import dev.rohit.buglens.IngestionEngine.format.LogFormat;

public class ProcessingContext {
    private LogFormat logFormat;

    public ProcessingContext() {
    }

    public LogFormat getLogFormat() {
        return logFormat;
    }

    public void setLogFormat(LogFormat format) {
        this.logFormat = format;
    }
}

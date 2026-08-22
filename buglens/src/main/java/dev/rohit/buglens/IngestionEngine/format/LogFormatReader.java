package dev.rohit.buglens.IngestionEngine.format;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONException;
import org.json.JSONObject;

public class LogFormatReader {
    private final String path;

    public LogFormatReader(String path) {
        this.path = path;
    }

    public JSONObject readFile() throws JSONException, IOException {
        String content = new String(Files.readAllBytes(Paths.get(path)));

        return new JSONObject(content);
    }
}
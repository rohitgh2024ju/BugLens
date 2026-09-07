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
        System.out.println("FORMAT FILE PATH: " +
        Paths.get(this.path).toAbsolutePath());

        System.out.println("FORMAT FILE EXISTS: " +
                Files.exists(Paths.get(this.path)));
        String content = new String(Files.readAllBytes(Paths.get(path)));

        return new JSONObject(content);
    }
}
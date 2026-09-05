package dev.rohit.buglens.IngestionEngine.Reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class LogReader {

    private final Path path;

    public LogReader(Path outputPath) {

        if (outputPath == null) {
            throw new IllegalArgumentException(
                    "Output path is required");
        }

        this.path = outputPath;
    }

    public JSONObject readFile(int n)
            throws JSONException, IOException {

        List<String> logList =
                new ArrayList<>();

        JSONObject logData =
                new JSONObject();

        try (
                BufferedReader reader =
                        Files.newBufferedReader(
                                path,
                                StandardCharsets.UTF_8)
        ) {

            String line;

            int count = 0;
            String fileId = null;
            String clientId = null;

            while (
                    (n <= 0 || count < n)
                            && (line = reader.readLine()) != null
            ) {

                JSONObject logObject =
                        new JSONObject(line);

                fileId =
                        logObject.optString(
                                "file_id",
                                null);

                clientId =
                        logObject.optString(
                                "client_id",
                                null);

                logList.add(
                        logObject.getString(
                                "raw_data"));

                count++;
            }

            logData.put(
                    "client_id",
                    clientId);

            logData.put(
                    "file_id",
                    fileId);

            logData.put(
                    "logs",
                    logList);

            return logData;
        }
    }
}
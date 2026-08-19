package dev.rohit.buglens.IngestionEngine;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IngestionEngine {

    public static void main(String[] args) {
        try {
            LogReader logReader = new LogReader("buglens/logs/output.jsonl");
            
            JSONObject logData = logReader.readFile(5); 
            System.out.println(logData.toString(2)); 
            
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
}

class LogReader {

    private final String path;

    public LogReader(String path) {
        this.path = path;
    }

    public JSONObject readFile(int n) throws JSONException, IOException {
        List<String> logList = new ArrayList<>();
        JSONObject logData = new JSONObject(); 
        
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            int count = 0;
            String fileId = null;

            while (count < n && (line = reader.readLine()) != null) {
                JSONObject logObject = new JSONObject(line);

                fileId = logObject.optString("file_id", null);
                logList.add(logObject.getString("raw_data"));
                
                count++;
            }

            logData.put("file_id", fileId);
            logData.put("logs", logList);

            return logData;
        }
    }
}
package dev.rohit.buglens.IngestionEngine;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import dev.rohit.buglens.IngestionEngine.Reader.LogReader;
import dev.rohit.buglens.IngestionEngine.Structure.LogFormatter;
import dev.rohit.buglens.IngestionEngine.format.FormatComparator;

public class IngestionEngine {

    public static void main(String[] args) {
        try {
            LogReader logReader = new LogReader("buglens/logs/output.jsonl");
            LogFormatter formatter = new LogFormatter();

            JSONObject logData = logReader.readFile(5);

            System.out.println(logData.toString(2));
            JSONArray logArray = logData.getJSONArray("logs");

            String rawLogLineTest = logArray.getString(0);

            String formattedLogLineTest = formatter.format(rawLogLineTest);

            // for (int i = 0; i < 5; i++) {
            // String logLine = logArray.getString(i);
            // String result = formatter.format(logLine);
            // System.out.println("Structural Format " + (i + 1) + " : " + result);
            // }

            // LogFormatReader logFormatReader = new LogFormatReader("buglens/Resources/log_formats.jsonl");
            // JSONObject formatArray = logFormatReader.readFile();

            // System.out.println(formatArray.getJSONArray("formats").toString(2));

            FormatComparator formatComparator = new FormatComparator(
                    formattedLogLineTest,
                    "buglens/Resources/log_formats.jsonl");

            List<String[]> result = formatComparator.compare();

            for (String[] entry : result) {
                System.out.println("Name: " + entry[0] + " | Parser: " + entry[1] + " | Confidence: " + entry[2]);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
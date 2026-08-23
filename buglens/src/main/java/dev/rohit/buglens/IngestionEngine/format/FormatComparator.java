package dev.rohit.buglens.IngestionEngine.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FormatComparator {
    private final String inputFormat;
    private final LogFormatReader logFormatReader;
    private final String fileId;

    public FormatComparator(String inputFormat, String filePath, String fileId) {
        this.inputFormat = inputFormat;
        this.logFormatReader = new LogFormatReader(filePath);
        this.fileId = fileId;
    }

    public List<String[]> compare() throws JSONException, IOException {
        List<String[]> resultCompare = new ArrayList<>();

        JSONObject formatsObject = logFormatReader.readFile();
        String[] inputFormatList = inputFormat.split("\\s+");
        JSONArray formatsArray = formatsObject.getJSONArray("formats");

        for (int i = 0; i < formatsArray.length(); i++) {
            JSONObject formatObj = formatsArray.getJSONObject(i);

            String formatName = formatObj.getString("name");
            String formatStructure = formatObj.getString("structure");
            String formatParser = formatObj.getString("parser");

            String[] formatStructureList = formatStructure.split("\\s+");

            int count = 0;
            int minLength = Math.min(inputFormatList.length, formatStructureList.length);

            int j = 0;
            while (j < minLength) {
                if (inputFormatList[j].equals(formatStructureList[j])) {
                    count++;
                }
                j++; 
            }

            double confidenceValue = ((double) count / Math.max(inputFormatList.length, formatStructureList.length))
                    * 100;
            String confidence = String.format("%.2f%%", confidenceValue);

            resultCompare.add(new String[] { fileId, formatName, formatParser, confidence });
        }

        return resultCompare;
    }
}
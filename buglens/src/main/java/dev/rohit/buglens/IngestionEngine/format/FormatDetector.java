package dev.rohit.buglens.IngestionEngine.format;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import dev.rohit.buglens.Configpaths.BugLensPaths;
import dev.rohit.buglens.IngestionEngine.Reader.LogReader;
import dev.rohit.buglens.IngestionEngine.Structure.LogFormatter;

public class FormatDetector {

        private List<LogFormat> formatList;

        public LogFormat detect(String clientId) {

                try {
                        Path outputDirectory = BugLensPaths.LOGS_DIR;
                        Path outputPath = outputDirectory.resolve("output-" + clientId + ".jsonl");

                        // Initialize LogReader and LogFormatter
                        LogReader logReader = new LogReader(
                                        outputPath);

                        LogFormatter formatter = new LogFormatter();

                        // Read log data
                        JSONObject logData = logReader.readFile(5);

                        String fileId = logData.getString("file_id");

                        JSONArray logArray = logData.getJSONArray("logs");

                        // Get a sample log line
                        String rawLogLineTest = logArray.getString(0);

                        // Normalize / format the raw log
                        String formattedLogLineTest = formatter.format(rawLogLineTest);

                        System.out.println("OUTPUT PATH: " + outputPath.toAbsolutePath());
                        System.out.println("RAW LOG SAMPLE: " + rawLogLineTest);
                        System.out.println("FORMATTED LOG SAMPLE: " + formattedLogLineTest);

                        System.out.println("RAW LOG:");
                        System.out.println(rawLogLineTest);

                        System.out.println("FORMATTED LOG:");
                        System.out.println(formattedLogLineTest);

                        // Compare with known log formats
                        FormatComparator formatComparator = new FormatComparator(
                                        formattedLogLineTest,
                                        BugLensPaths.LOG_FORMATS_FILE.toString(),
                                        fileId);

                        // Raw comparison results
                        List<String[]> result = formatComparator.compare();

                        System.out.println("COMPARISON RESULT SIZE:" + result.size());
                        System.out.println("MATCHES FOUND: " + result.size());

                        // Convert String[] -> LogFormat
                        this.formatList = result.stream()
                                        .map(entry -> new LogFormat(
                                                        entry[0],
                                                        entry[1],
                                                        entry[2],
                                                        Double.parseDouble(entry[3].replace("%", ""))))
                                        .collect(Collectors.toList());

                        System.out.println(result.size());
                        // No matching formats
                        if (this.formatList.isEmpty()) {
                                throw new IllegalArgumentException("Unsupported or unrecognized log format");
                        }

                        // Sort by confidence, highest first
                        this.formatList.sort(
                                        Comparator
                                                        .comparingDouble(LogFormat::getConfidence)
                                                        .reversed());

                        // Get the best match
                        LogFormat bestFormat = this.formatList.get(0);

                        System.out.println(
                                        "Top Match: "
                                                        + bestFormat.getParser());

                        return bestFormat;

                } catch (Exception e) {
                        throw new IllegalStateException(
                                        "Failed to detect log format for client: " + clientId,
                                        e);
                }
        }

        public void getDetectionDetails() {

                // Check whether detection has been performed
                if (this.formatList == null
                                || this.formatList.isEmpty()) {

                        System.out.println(
                                        "No detection results available.");

                        return;
                }

                // Print all detected formats
                for (LogFormat entry : this.formatList) {

                        System.out.println(
                                        "File_Id: " + entry.getFileId()
                                                        + " | Name: " + entry.getName()
                                                        + " | Parser: " + entry.getParser()
                                                        + " | Confidence: "
                                                        + entry.getConfidence());
                }
        }
}
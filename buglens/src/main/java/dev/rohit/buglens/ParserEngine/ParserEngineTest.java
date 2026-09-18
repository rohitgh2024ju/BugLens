package dev.rohit.buglens.ParserEngine;

import dev.rohit.buglens.IngestionEngine.format.LogFormat;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Path;

public class ParserEngineTest {

    public static void main(String[] args) {

        /*
         * Path to an existing ingestion output file.
         *
         * Example:
         * logs/output-test.jsonl
         *
         * Replace this with one of your actual files.
         */
        Path inputPath = Path.of(
                "buglens/test-data/springboot-test.jsonl"
        );

        /*
         * This represents the result that would normally
         * come from FormatDetector.
         */
        LogFormat logFormat = new LogFormat(
                "test-file",
                "Spring Boot / Java Standard Log",
                "SpringBootParser",
                100.0
        );

        System.out.println(
                "===== PARSER ENGINE TEST ====="
        );

        System.out.println(
                "INPUT: "
                        + inputPath.toAbsolutePath()
        );

        System.out.println(
                "FORMAT: "
                        + logFormat.getName()
        );

        System.out.println(
                "PARSER: "
                        + logFormat.getParser()
        );

        try {

            /*
             * Create ParserEngine.
             */
            ParserEngine parserEngine =
                    new ParserEngine(inputPath);

            /*
             * Run the selected BLR parser
             * over all logs.
             */
            JSONArray parsedLogs =
                    parserEngine.runParser(logFormat);

            System.out.println();
            System.out.println(
                    "===== RESULT ====="
            );

            System.out.println(
                    "PARSED LOG COUNT: "
                            + parsedLogs.length()
            );

            /*
             * Display the first parsed event.
             */
            if (!parsedLogs.isEmpty()) {

                JSONObject firstLog =
                        parsedLogs.getJSONObject(0);

                System.out.println();
                System.out.println(
                        "FIRST PARSED LOG:"
                );

                System.out.println(
                        firstLog.toString(4)
                );
            }

        } catch (Exception e) {

            System.err.println(
                    "ParserEngine test failed:"
            );

            e.printStackTrace();
        }
    }
}
package dev.rohit.buglens.IngestionEngine;

import dev.rohit.buglens.BLR.Bundles.SpringBootBundler.SpringBootParser;

import org.json.JSONObject;

public class SpringBootParserTest {

    public static void main(String[] args) {

        SpringBootParser parser = new SpringBootParser();

        // ==========================================
        // DEFAULT CONFIGURATION
        // ==========================================

        String defaultLog = "2026-09-06 10:00:01.123 INFO [main] " +
                "com.buglens.order.OrderService : " +
                "received order request requestId=req-001";

        System.out.println(
                "===== DEFAULT LOG TEST =====");

        JSONObject defaultParsed = parser.parse(defaultLog);

        parser.printParsedData(defaultParsed);

        // ==========================================
        // PID CONFIGURATION
        // ==========================================

        String pidLog = "2026-09-06T23:59:12.443+05:30 INFO 25024 --- " +
                "[buglens] [           main] " +
                "o.s.b.a.e.web.EndpointLinksResolver : " +
                "Exposing 1 endpoint(s)";

        System.out.println();
        System.out.println(
                "===== PID LOG TEST =====");

        JSONObject pidParsed = parser.parse(pidLog);

        parser.printParsedData(pidParsed);

        // ==========================================
        // KEY-VALUE TEST
        // ==========================================

        String keyValueLog = "2026-09-06T23:59:13.100+05:30 ERROR 25024 --- " +
                "[buglens] [           main] " +
                "dev.rohit.buglens.OrderService : " +
                "Order failed requestId=req-123 " +
                "traceId=trace-456 status=500";

        System.out.println();
        System.out.println(
                "===== KEY-VALUE TEST =====");

        JSONObject keyValueParsed = parser.parse(keyValueLog);

        parser.printParsedData(keyValueParsed);

        // ==========================================
        // UNKNOWN CONFIGURATION
        // ==========================================

        String unknownLog = "2026/09/06 | INFO | OrderService | " +
                "Order received | requestId=req-001";

        System.out.println();
        System.out.println(
                "===== UNKNOWN LOG TEST =====");

        JSONObject unknownParsed = parser.parse(unknownLog);

        parser.printParsedData(unknownParsed);
    }
}
package dev.rohit.buglens.BLR;

import dev.rohit.buglens.IngestionEngine.format.LogFormat;
import org.json.JSONObject;

public class BLRDispatcherTest {

    public static void main(String[] args) {

        // Pretend this came from FormatDetector
        LogFormat format = new LogFormat(
                "test-file",
                "springBoot",
                "Spring Boot / Java Standard Log",
                "SpringBootParser",
                100.0);

        String rawLogLine = "2026-09-06T23:59:12.443+05:30  INFO 25024 --- [buglens] [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 1 endpoint(s) beneath base path '/actuator'";

        // Create dispatcher
        BLRDispatcher dispatcher = new BLRDispatcher();

        // Dispatcher finds the correct parser automatically
        JSONObject result = dispatcher.parse(
                format,
                rawLogLine);

        System.out.println(
                "===== DISPATCHER TEST =====");

        System.out.println(
                result.toString(4));
    }
}
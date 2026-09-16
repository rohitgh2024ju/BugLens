package dev.rohit.buglens.BLR.Bundles.SpringBootBundler.Configurations;

import java.util.List;
import java.util.regex.Pattern;

public class SpringBootDefaultConfig implements SpringBootConfig {

        @Override
        public String getConfigId() {
                return "spring_boot_default";
        }

        @Override
        public List<FieldPattern> getFieldPatterns() {
                return List.of(
                                new FieldPattern(
                                                "timestamp",
                                                Pattern.compile(
                                                                "\\d{4}-\\d{2}-\\d{2}\\s+" +
                                                                                "\\d{2}:\\d{2}:\\d{2}\\.\\d{3}"),
                                                1,
                                                true,
                                                3.0,
                                                false),

                                new FieldPattern(
                                                "severity",
                                                Pattern.compile(
                                                                "INFO|WARN|ERROR|DEBUG|TRACE|FATAL"),
                                                2,
                                                true,
                                                2.0,
                                                false),

                                new FieldPattern(
                                                "thread",
                                                Pattern.compile(
                                                                "\\[[^\\]]+\\]"),
                                                3,
                                                true,
                                                2.0,
                                                false),

                                new FieldPattern(
                                                "logger",
                                                Pattern.compile(
                                                                "[a-zA-Z_$][\\w$]*(\\.[a-zA-Z_$][\\w$]*)*"),
                                                4,
                                                true,
                                                3.0,
                                                false),

                                new FieldPattern(
                                                "message",
                                                Pattern.compile(".*"),
                                                6,
                                                false,
                                                0.0,
                                                true));
        }

        @Override
        public List<StructurePattern> getStructurePatterns() {
                return List.of(

                                new StructurePattern(
                                                "separator",
                                                5,
                                                Pattern.compile(":"),
                                                1.0));
        }
}
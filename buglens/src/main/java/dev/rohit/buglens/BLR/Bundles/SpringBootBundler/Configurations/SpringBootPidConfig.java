package dev.rohit.buglens.BLR.Bundles.SpringBootBundler.Configurations;

import java.util.List;
import java.util.regex.Pattern;

public class SpringBootPidConfig implements SpringBootConfig {

        @Override
        public String getConfigId() {
                return "spring_boot_pid";
        }

        @Override
        public List<FieldPattern> getFieldPatterns() {
                return List.of(
                                new FieldPattern(
                                                "timestamp",
                                                Pattern.compile(
                                                                "\\d{4}-\\d{2}-\\d{2}T" +
                                                                                "\\d{2}:\\d{2}:\\d{2}\\.\\d{3}" +
                                                                                "[+-]\\d{2}:\\d{2}"),
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
                                                "pid",
                                                Pattern.compile("\\d+"),
                                                3,
                                                true,
                                                2.0,
                                                false),

                                new FieldPattern(
                                                "application",
                                                Pattern.compile(
                                                                "\\[[^\\]]+\\]"),
                                                5,
                                                true,
                                                1.0,
                                                false),

                                new FieldPattern(
                                                "thread",
                                                Pattern.compile(
                                                                "\\[[^\\]]+\\]"),
                                                6,
                                                true,
                                                2.0,
                                                false),

                                new FieldPattern(
                                                "logger",
                                                Pattern.compile(
                                                                "[a-zA-Z_$][\\w$]*(\\.[a-zA-Z_$][\\w$]*)*"),
                                                7,
                                                true,
                                                3.0,
                                                false),

                                new FieldPattern(
                                                "message",
                                                Pattern.compile(".*"),
                                                9,
                                                false,
                                                0.0,
                                                true));
        }

        @Override
        public List<StructurePattern> getStructurePatterns() {
                return List.of(

                                new StructurePattern(
                                                "separator",
                                                4,
                                                Pattern.compile("---"),
                                                1.0),

                                new StructurePattern(
                                                "separator",
                                                8,
                                                Pattern.compile(":"),
                                                1.0));
        }
}
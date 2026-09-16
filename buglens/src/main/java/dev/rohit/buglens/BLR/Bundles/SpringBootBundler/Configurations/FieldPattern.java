package dev.rohit.buglens.BLR.Bundles.SpringBootBundler.Configurations;

import java.util.regex.Pattern;

public record FieldPattern(
        String fieldName,
        Pattern pattern,
        int position,
        boolean required,
        double weight,
        boolean remainder) {
}
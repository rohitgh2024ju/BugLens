package dev.rohit.buglens.BLR.Bundles.SpringBootBundler.Configurations;

import java.util.regex.Pattern;

public record StructurePattern(
    String name,
    int position,
    Pattern pattern,
    double weight
) {

}

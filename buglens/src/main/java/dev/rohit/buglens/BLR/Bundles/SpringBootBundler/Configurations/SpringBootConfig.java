package dev.rohit.buglens.BLR.Bundles.SpringBootBundler.Configurations;

import java.util.List;

public interface SpringBootConfig {

    String getConfigId();

    List<FieldPattern> getFieldPatterns();
    List<StructurePattern> getStructurePatterns();
}
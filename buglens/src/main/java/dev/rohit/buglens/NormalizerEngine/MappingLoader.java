package dev.rohit.buglens.NormalizerEngine;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class MappingLoader {

    private static final String CONFIG_PATH = "buglens/config/normalization-mapping.xml";

    public void details() {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            NormalizationMappings mappings = xmlMapper.readValue(
                    new File(CONFIG_PATH), NormalizationMappings.class);

            if (mappings.getFormats() != null) {
                for (FormatConfig format : mappings.getFormats()) {
                    System.out.println("=== Format ID: " + format.getId() + " ===");
                    if (format.getFields() != null) {
                        for (FieldMapping field : format.getFields()) {
                            System.out.println("  Source: " + field.getSource() + " -> Target: " + field.getTarget());
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to parse " + CONFIG_PATH + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<FieldMapping> loadMapper(String targetFormat) {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            File configFile = new File(CONFIG_PATH);

            if (!configFile.exists()) {
                System.err.println("Configuration file not found at path: " + configFile.getAbsolutePath());
                return Collections.emptyList();
            }

            NormalizationMappings mappings = xmlMapper.readValue(configFile, NormalizationMappings.class);

            if (mappings.getFormats() != null) {
                return mappings.getFormats().stream()
                        .filter(f -> targetFormat.equals(f.getId()))
                        .map(FormatConfig::getFields)
                        .findFirst()
                        .orElse(Collections.emptyList());
            }
        } catch (IOException e) {
            System.err.println("Failed to parse " + CONFIG_PATH + ": " + e.getMessage());
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    public static void main(String[] args) {
        MappingLoader mappingLoader = new MappingLoader();
        mappingLoader.details();

        List<FieldMapping> springBootFields = mappingLoader.loadMapper("spring_boot");
        System.out.println("\nLoaded " + springBootFields.size() + " fields for 'spring_boot':");
        springBootFields.forEach(f -> System.out.println(f.getSource() + " -> " + f.getTarget()));
    }
}

// Top-level container matching <NormalizationMappings>
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "NormalizationMappings")
class NormalizationMappings {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Format")
    private List<FormatConfig> formats;

    public List<FormatConfig> getFormats() {
        return formats;
    }

    public void setFormats(List<FormatConfig> formats) {
        this.formats = formats;
    }
}

// Container matching each <Format id="...">
@JsonIgnoreProperties(ignoreUnknown = true)
class FormatConfig {

    @JacksonXmlProperty(isAttribute = true)
    private String id;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Field")
    private List<FieldMapping> fields;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<FieldMapping> getFields() {
        return fields;
    }

    public void setFields(List<FieldMapping> fields) {
        this.fields = fields;
    }
}

// Container matching each <Field source="..." target="..."/>
class FieldMapping {

    @JacksonXmlProperty(isAttribute = true)
    private String source;

    @JacksonXmlProperty(isAttribute = true)
    private String target;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
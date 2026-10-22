package dev.rohit.buglens.IncidentMetadata;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor 
public class IncidentMetadata {
    private String title;
    private String description;
    private String service;
    private String exceptionType;
    private List<String> tags;
}

package dev.rohit.buglens.QueryLayer.query;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryCriteria {
    private String from;
    private String to;

    private Map<String, Object> source;
    private Map<String, Object> occurrence;
    private Map<String, Object> context;
    private Map<String, Object> metadata;
}

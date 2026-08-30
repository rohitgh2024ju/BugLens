package dev.rohit.buglens.GraphEngine.model;

import dev.rohit.buglens.NormalizerEngine.model.NormalizedEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventNode {

    private String id;

    private NormalizedEvent event;
}

// two event nodes with same event id should represent the same graph vertex
package dev.rohit.buglens.QueryLayer.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor 
public class EventQuery {
    private QueryCriteria criteria;
    private int limit;
}

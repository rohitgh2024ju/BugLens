package dev.rohit.buglens.Dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class SeverityBreakdown {
    private String name;
    private long count;
    private double percentage;
}

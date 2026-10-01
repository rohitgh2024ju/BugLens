package dev.rohit.buglens.Dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class ServiceBreakdown {
    private String service;
    private long errorCount;
}

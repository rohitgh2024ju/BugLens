package dev.rohit.buglens.IngestionEngine.util;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter 
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HashModel {
    private String hashId;
    private String clientId;
}

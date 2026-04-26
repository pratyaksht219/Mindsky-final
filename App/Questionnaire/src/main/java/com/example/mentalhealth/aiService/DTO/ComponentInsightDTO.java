package com.example.mentalhealth.aiService.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComponentInsightDTO {

    private String componentId;              // comp2_sleep_latency
    private String componentName;            // human readable
    private Double score;                    // component score
    private String interpretationHint;       // OPTIONAL short phrase
    private List<String> contributingQuestionIds;
}
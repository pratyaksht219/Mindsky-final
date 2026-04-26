package com.example.mentalhealth.aiService.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssessmentSummaryDTO {

    private Double finalScore;              // REQUIRED
    private String severityLabel;            // REQUIRED
    private String clinicalDescription;      // from scoring rules
    private Double minScore;                 // optional but recommended
    private Double maxScore;                 // optional but recommended
}
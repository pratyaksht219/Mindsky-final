package com.example.mentalhealth.Assessment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResult {
    private String questionnaireId;
    private double finalScore;
    private String severityLabel;
    private String description;
    private Map<String, Double> componentScores;

}

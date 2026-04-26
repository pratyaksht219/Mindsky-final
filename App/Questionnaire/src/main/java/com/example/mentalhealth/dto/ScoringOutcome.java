package com.example.mentalhealth.dto;

import com.example.mentalhealth.Assessment.AssessmentBreakdown;
import com.example.mentalhealth.Assessment.AssessmentResult;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScoringOutcome {
    private AssessmentResult result;
    private AssessmentBreakdown breakdown;
}
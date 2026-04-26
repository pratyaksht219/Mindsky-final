package com.example.mentalhealth.aiService.DTO;

import com.example.mentalhealth.Assessment.AssessmentBreakdown;
import com.example.mentalhealth.Assessment.AssessmentResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIServiceRequestDTO {


    private RequestMetadataDTO metadata;

    private AssessmentSummaryDTO assessment;

    private List<ComponentInsightDTO> components;

    private List<RiskSignalDTO> riskSignals;

    private Map<String, Object> contextHints;

    private AIResponseConstraintsDTO constraints;
}
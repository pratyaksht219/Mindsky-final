package com.example.mentalhealth.aiService;

import com.example.mentalhealth.Assessment.AssessmentBreakdown;
import com.example.mentalhealth.Assessment.AssessmentResult;
import com.example.mentalhealth.aiService.DTO.AIServiceRequestDTO;

public interface AIContextBuilder {

    AIServiceRequestDTO build(
            AssessmentResult assessmentResult,
            AssessmentBreakdown breakdown
    );
}
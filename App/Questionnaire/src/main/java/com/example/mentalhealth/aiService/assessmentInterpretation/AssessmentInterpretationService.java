package com.example.mentalhealth.aiService.assessmentInterpretation;

import com.example.mentalhealth.Assessment.AssessmentBreakdown;
import com.example.mentalhealth.Assessment.AssessmentResult;
import com.example.mentalhealth.aiService.DTO.AIServiceResponseDTO;

public interface AssessmentInterpretationService {

    AIServiceResponseDTO interpret(
            AssessmentResult result,
            AssessmentBreakdown breakdown
    );
}
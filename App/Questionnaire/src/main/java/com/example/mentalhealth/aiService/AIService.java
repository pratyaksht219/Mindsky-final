package com.example.mentalhealth.aiService;

import com.example.mentalhealth.Assessment.AssessmentBreakdown;
import com.example.mentalhealth.Assessment.AssessmentResult;
import com.example.mentalhealth.aiService.DTO.AIServiceRequestDTO;
import com.example.mentalhealth.aiService.DTO.AIServiceResponseDTO;

public interface AIService {
    AIServiceResponseDTO getAiServiceResponse(AIServiceRequestDTO request);
}

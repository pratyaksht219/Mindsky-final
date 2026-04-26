package com.example.mentalhealth.dto;

import com.example.mentalhealth.Assessment.AssessmentBreakdown;
import com.example.mentalhealth.Assessment.AssessmentPhase;
import com.example.mentalhealth.Assessment.AssessmentResult;
import com.example.mentalhealth.aiService.DTO.AIServiceResponseDTO;
import com.example.mentalhealth.questionnaire.DTO.QuestionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinalResponseDTO {

    private String sessionId;
    private AssessmentPhase phase; // QUESTIONNAIRE | COMPLETED

    private QuestionDTO nextQuestion;
    private AssessmentResult result;
    private AssessmentBreakdown breakdown;
    private AIServiceResponseDTO aiServiceResponse;
}

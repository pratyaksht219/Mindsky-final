package com.example.mentalhealth.flow;

import com.example.mentalhealth.answers.AnswerSubmissionDTO;
import com.example.mentalhealth.Assessment.AssessmentResponse;
import com.example.mentalhealth.Assessment.StartAssessmentRequest;
import com.example.mentalhealth.dto.FinalResponseDTO;

public interface AssessmentFlowOrchestrator {

    FinalResponseDTO startAssessment(StartAssessmentRequest request);

    FinalResponseDTO submitAnswer(AnswerSubmissionDTO submission);
}
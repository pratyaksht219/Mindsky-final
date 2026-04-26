package com.example.mentalhealth.Assessment;

import com.example.mentalhealth.questionnaire.DTO.QuestionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssessmentResponse {

    private String sessionId;
    private AssessmentPhase phase; // QUESTIONNAIRE | COMPLETED

    private QuestionDTO nextQuestion;

    private AssessmentResult result;
    private AssessmentBreakdown breakdown;

    public static AssessmentResponse question(
            String sessionId,
            QuestionDTO question
    ) {
        return new AssessmentResponse(
                sessionId,
                AssessmentPhase.QUESTIONNAIRE,
                question,
                null,
                null
        );
    }

    public static AssessmentResponse completed(
            String sessionId,
            AssessmentResult result,
            AssessmentBreakdown breakdown
    ) {
        return new AssessmentResponse(
                sessionId,
                AssessmentPhase.COMPLETED,
                null,
                result,
                breakdown
        );
    }
}
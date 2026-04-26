package com.example.mentalhealth.Assessment;

import com.example.mentalhealth.answers.AnswerSubmissionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssessmentRequestEnvelope {

    /**
     * START or ANSWER
     */
    private RequestType type;

    private StartAssessmentRequest startRequest;

    private AnswerSubmissionDTO answerSubmission;

    public enum RequestType {
        START,
        ANSWER
    }
}
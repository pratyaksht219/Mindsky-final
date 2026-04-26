package com.example.mentalhealth.controller;

import com.example.mentalhealth.answers.AnswerSubmissionDTO;
import com.example.mentalhealth.Assessment.AssessmentResponse;
import com.example.mentalhealth.Assessment.StartAssessmentRequest;
import com.example.mentalhealth.dto.FinalResponseDTO;
import com.example.mentalhealth.flow.AssessmentFlowOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/assessment")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentFlowOrchestrator assessmentFlowOrchestrator;

    /**
     * Start a new assessment for a given questionnaire
     * Example body:
     * {
     *   "questionnaireId": "gad7"
     * }
     */
    @PostMapping("/start")
    public ResponseEntity<FinalResponseDTO> startAssessment(
            @RequestBody StartAssessmentRequest request
    ) {
        FinalResponseDTO response =
                assessmentFlowOrchestrator.startAssessment(request);

        return ResponseEntity.ok(response);
    }

    /**
     * Submit an answer for the current session
     * Example body:
     * {
     *   "sessionId": "abc-123",
     *   "questionId": "gad7_q1",
     *   "responses": {
     *     "gad7_q1_score": 2
     *   }
     * }
     */
    @PostMapping("/answer")
    public ResponseEntity<FinalResponseDTO> submitAnswer(
            @RequestBody AnswerSubmissionDTO submission
    ) {
        FinalResponseDTO response =
                assessmentFlowOrchestrator.submitAnswer(submission);

        return ResponseEntity.ok(response);
    }
}
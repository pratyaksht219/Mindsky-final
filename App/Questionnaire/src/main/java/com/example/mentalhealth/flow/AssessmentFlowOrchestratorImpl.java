package com.example.mentalhealth.flow;

import com.example.mentalhealth.Assessment.AssessmentPhase;
import com.example.mentalhealth.Assessment.StartAssessmentRequest;
import com.example.mentalhealth.aiService.DTO.AIServiceResponseDTO;
import com.example.mentalhealth.aiService.assessmentInterpretation.AssessmentInterpretationService;
import com.example.mentalhealth.answers.AnswerSubmissionDTO;
import com.example.mentalhealth.dto.*;
import com.example.mentalhealth.questionnaire.DTO.QuestionDTO;
import com.example.mentalhealth.questionnaire.QuestionnaireService;
import com.example.mentalhealth.questionnaire.factory.QuestionnaireServiceFactory;
import com.example.mentalhealth.scoring.ScoringEngines.ScoringEngine;
import com.example.mentalhealth.session.QuestionnaireSession;
import com.example.mentalhealth.session.QuestionnaireSessionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssessmentFlowOrchestratorImpl
        implements AssessmentFlowOrchestrator {

    private final QuestionnaireSessionRegistry sessionRegistry;
    private final QuestionnaireServiceFactory questionnaireServiceFactory;
    private final ScoringEngine scoringEngine;
    private final AssessmentInterpretationService assessmentInterpretationService;

    @Override
    public FinalResponseDTO startAssessment(
            StartAssessmentRequest request
    ) {

        // Create session
        QuestionnaireSession session =
                sessionRegistry.create(request.getQuestionnaireId());

        // Resolve questionnaire service
        QuestionnaireService questionnaireService =
                questionnaireServiceFactory.getService(
                        request.getQuestionnaireId()
                );

        // First question
        QuestionDTO firstQuestion =
                questionnaireService.getNextQuestion(session);

        return new FinalResponseDTO(
                session.getSessionId(),
                AssessmentPhase.QUESTIONNAIRE,
                firstQuestion,
                null,
                null,
                null
        );
    }

    @Override
    public FinalResponseDTO submitAnswer(
            AnswerSubmissionDTO submission
    ) {

        // Load session
        QuestionnaireSession session =
                sessionRegistry.get(submission.getSessionId());

        QuestionnaireService questionnaireService =
                questionnaireServiceFactory.getService(
                        session.getQuestionnaireId()
                );

        // Submit answer
        questionnaireService.submitAnswer(submission, session);

        // If questionnaire still in progress
        if (!questionnaireService.isCompleted(session)) {

            QuestionDTO nextQuestion =
                    questionnaireService.getNextQuestion(session);

            return new FinalResponseDTO(
                    session.getSessionId(),
                    AssessmentPhase.QUESTIONNAIRE,
                    nextQuestion,
                    null,
                    null,
                    null
            );
        }

        // Questionnaire completed → scoring
        ScoringOutcome outcome =
                scoringEngine.score(
                        session.getQuestionnaireId(),
                        session.getAnswerStore()
                );

        // Call AI interpretation service
        AIServiceResponseDTO aiResponse =
                assessmentInterpretationService.interpret(
                        outcome.getResult(),
                        outcome.getBreakdown()
                );

        FinalResponseDTO finalResponse = new FinalResponseDTO(
                session.getSessionId(),
                AssessmentPhase.COMPLETED,
                null,
                outcome.getResult(),
                outcome.getBreakdown(),
                aiResponse
        );

        // Cleanup session
        sessionRegistry.remove(session.getSessionId());

        return finalResponse;
    }
}
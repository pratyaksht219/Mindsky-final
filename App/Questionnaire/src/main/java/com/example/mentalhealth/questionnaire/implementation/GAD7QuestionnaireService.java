package com.example.mentalhealth.questionnaire.implementation;

import com.example.mentalhealth.answers.AnswerExtractor;
import com.example.mentalhealth.answers.AnswerSubmissionDTO;
import com.example.mentalhealth.questionnaire.DTO.QuestionDTO;
import com.example.mentalhealth.questionnaire.DTO.QuestionnaireDTO;
import com.example.mentalhealth.questionnaire.DTO.ResponseType;
import com.example.mentalhealth.questionnaire.QuestionnaireService;
import com.example.mentalhealth.questionnaire.registry.QuestionnaireRegistry;
import com.example.mentalhealth.session.QuestionnaireSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
@Slf4j
@Service
@RequiredArgsConstructor
public class GAD7QuestionnaireService implements QuestionnaireService {

    private final QuestionnaireRegistry questionnaireRegistry;

    @Override
    public String supportedQuestionnaireId() {
        return "gad7";
    }

    private QuestionnaireDTO getDefinition() {
        return questionnaireRegistry
                .getById(supportedQuestionnaireId())
                .orElseThrow(() ->
                        new IllegalStateException("GAD-7 questionnaire not found")
                );
    }

    @Override
    public QuestionDTO getNextQuestion(QuestionnaireSession session) {
        QuestionnaireDTO questionnaire = getDefinition();

        int index = session.getCurrentQuestionIndex();
        if (index >= questionnaire.getQuestions().size()) {
            throw new IllegalStateException("GAD-7 questionnaire already completed");
        }

        return questionnaire.getQuestions().get(index);
    }

    @Override
    public void submitAnswer(
            AnswerSubmissionDTO submission,
            QuestionnaireSession session
    ) {

        // Questionnaire integrity
        if (!supportedQuestionnaireId().equals(submission.getQuestionnaireId())) {
            throw new IllegalArgumentException(
                    "Invalid questionnaire_id for GAD-7: "
                            + submission.getQuestionnaireId()
            );
        }

        QuestionDTO question = getNextQuestion(session);

        log.info(
                "SubmitAnswer: session={}, incomingQuestion={}, expectedQuestion={}, index={}",
                submission.getSessionId(),
                submission.getQuestionId(),
                question.getId(),
                session.getCurrentQuestionIndex()
        );

        //  Enforce strict order
        if (!question.getId().equals(submission.getQuestionId())) {
            throw new IllegalArgumentException(
                    "Question ID mismatch. Expected "
                            + question.getId()
                            + " but got "
                            + submission.getQuestionId()
            );
        }

        //  Enforce response type
        if (question.getResponseFormat().getType() != ResponseType.SCALE) {
            throw new IllegalStateException(
                    "GAD-7 only supports SCALE responses"
            );
        }

        //  Extract response
        Double value = AnswerExtractor.extractScaleValue(submission, question);

        //  GAD-7 scale guard (0–3)
        if (value < 0 || value > 3) {
            throw new IllegalArgumentException(
                    "GAD-7 response must be between 0 and 3"
            );
        }

        //  Store (single writer)
        session.getAnswerStore().put(
                question.getId(),
                question.getResponseFormat().getResponseKey(),
                value
        );

        //  Advance exactly once
        session.incrementQuestionIndex();

        log.info(
                "After submit: index={}",
                session.getCurrentQuestionIndex()
        );
    }

    @Override
    public boolean isCompleted(QuestionnaireSession session) {
        return session.getCurrentQuestionIndex()
                >= getDefinition().getQuestions().size();
    }

    /* ---------- helpers ---------- */

    private Double extractNumeric(Object value) {
        if (value instanceof Number n) {
            return n.doubleValue();
        }
        throw new IllegalArgumentException(
                "GAD-7 response must be numeric"
        );
    }
}
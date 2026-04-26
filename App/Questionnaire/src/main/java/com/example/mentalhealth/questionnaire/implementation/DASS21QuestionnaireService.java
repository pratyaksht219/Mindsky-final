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
public class DASS21QuestionnaireService implements QuestionnaireService {

    private final QuestionnaireRegistry questionnaireRegistry;

    @Override
    public String supportedQuestionnaireId() {
        return "dass21";
    }

    private QuestionnaireDTO getDefinition() {
        return questionnaireRegistry
                .getById(supportedQuestionnaireId())
                .orElseThrow(() ->
                        new IllegalStateException("DASS-21 questionnaire not found")
                );
    }

    @Override
    public QuestionDTO getNextQuestion(QuestionnaireSession session) {
        QuestionnaireDTO questionnaire = getDefinition();

        int index = session.getCurrentQuestionIndex();
        if (index >= questionnaire.getQuestions().size()) {
            throw new IllegalStateException("DASS-21 questionnaire already completed");
        }

        return questionnaire.getQuestions().get(index);
    }

    @Override
    public void submitAnswer(
            AnswerSubmissionDTO submission,
            QuestionnaireSession session
    ) {

        /* Questionnaire integrity (CRITICAL) */
        if (submission.getQuestionnaireId() == null) {
            throw new IllegalArgumentException(
                    "questionnaire_id is required in AnswerSubmissionDTO"
            );
        }

        if (!supportedQuestionnaireId().equals(submission.getQuestionnaireId())) {
            throw new IllegalArgumentException(
                    "Invalid questionnaire_id for DASS-21: "
                            + submission.getQuestionnaireId()
            );
        }

        QuestionDTO question = getNextQuestion(session);

        log.info(
                "DASS-21 submit: session={}, incomingQuestion={}, expectedQuestion={}, index={}",
                submission.getSessionId(),
                submission.getQuestionId(),
                question.getId(),
                session.getCurrentQuestionIndex()
        );

        /* Strict order enforcement */
        if (!question.getId().equals(submission.getQuestionId())) {
            throw new IllegalArgumentException(
                    "Question ID mismatch. Expected "
                            + question.getId()
                            + " but got "
                            + submission.getQuestionId()
            );
        }

        /* Response type guard */
        if (question.getResponseFormat().getType() != ResponseType.SCALE) {
            throw new IllegalStateException(
                    "DASS-21 only supports SCALE responses"
            );
        }

        /* Extract response (STRICT TYPE) */
//        Map<String, Object> responses = submission.getResponses();
//        String responseKey = question.getResponseFormat().getResponseKey();
//
//        if (!responses.containsKey(responseKey)) {
//            throw new IllegalArgumentException(
//                    "Missing response for key: " + responseKey
//            );
//        }
//
//        Double value = extractNumeric(responses.get(responseKey));


        Double value = AnswerExtractor.extractScaleValue(submission, question);

        /*  Scale guard (0–3) */
        if (value < 0 || value > 3) {
            throw new IllegalArgumentException(
                    "DASS-21 response must be between 0 and 3"
            );
        }

        /*  Store (single writer) */
        session.getAnswerStore().put(
                question.getId(),
                question.getResponseFormat().getResponseKey(),
                value
        );

        /*  Advance EXACTLY once */
        session.incrementQuestionIndex();

        log.info(
                "DASS-21 after submit: index={}",
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
                "DASS-21 response must be numeric"
        );
    }
}
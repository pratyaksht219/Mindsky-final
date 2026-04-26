package com.example.mentalhealth.questionnaire.implementation;

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

import java.time.LocalTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PSQIQuestionnaireService implements QuestionnaireService {

    private final QuestionnaireRegistry questionnaireRegistry;

    @Override
    public String supportedQuestionnaireId() {
        return "psqi";
    }

    private QuestionnaireDTO getDefinition() {
        return questionnaireRegistry
                .getById(supportedQuestionnaireId())
                .orElseThrow(() ->
                        new IllegalStateException("PSQI questionnaire not found")
                );
    }

    @Override
    public QuestionDTO getNextQuestion(QuestionnaireSession session) {

        QuestionnaireDTO questionnaire = getDefinition();

        int index = session.getCurrentQuestionIndex();

        if (index >= questionnaire.getQuestions().size()) {
            throw new IllegalStateException("PSQI questionnaire already completed");
        }

        return questionnaire.getQuestions().get(index);
    }

    @Override
    public void submitAnswer(
            AnswerSubmissionDTO submission,
            QuestionnaireSession session
    ) {

        /* =====================================================
           SESSION + QUESTIONNAIRE INTEGRITY
           ===================================================== */

        if (submission.getQuestionnaireId() == null) {
            throw new IllegalArgumentException(
                    "questionnaire_id is required in AnswerSubmissionDTO"
            );
        }

        if (!supportedQuestionnaireId().equals(submission.getQuestionnaireId())) {
            throw new IllegalArgumentException(
                    "Invalid questionnaire_id for PSQI: "
                            + submission.getQuestionnaireId()
            );
        }

        if (!session.getQuestionnaireId().equals(supportedQuestionnaireId())) {
            throw new IllegalStateException(
                    "Session questionnaire mismatch"
            );
        }

        if (!session.getSessionId().equals(submission.getSessionId())) {
            throw new IllegalStateException(
                    "Session ID mismatch"
            );
        }

        if (session.isCompleted()) {
            throw new IllegalStateException(
                    "PSQI session already completed"
            );
        }

        QuestionDTO question = getNextQuestion(session);

        log.info(
                "PSQI submit: session={}, incomingQuestion={}, expectedQuestion={}, index={}",
                submission.getSessionId(),
                submission.getQuestionId(),
                question.getId(),
                session.getCurrentQuestionIndex()
        );

        /* =====================================================
           STRICT QUESTION ORDER ENFORCEMENT
           ===================================================== */

        if (!question.getId().equals(submission.getQuestionId())) {
            throw new IllegalArgumentException(
                    "Question ID mismatch. Expected "
                            + question.getId()
                            + " but got "
                            + submission.getQuestionId()
            );
        }

        /* =====================================================
           EXTRACT RESPONSE SAFELY
           ===================================================== */

        Object rawValue;

        if (submission.getAnswer() != null) {

            rawValue = submission.getAnswer();

        } else {

            Map<String, Object> responses = submission.getResponses();

            if (responses == null) {
                throw new IllegalArgumentException(
                        "Answer or responses must be provided"
                );
            }

            String responseKey = question.getResponseFormat().getResponseKey();

            if (!responses.containsKey(responseKey)) {
                throw new IllegalArgumentException(
                        "Missing response for key: " + responseKey
                );
            }

            rawValue = responses.get(responseKey);
        }

        String responseKey = question.getResponseFormat().getResponseKey();
        ResponseType type = question.getResponseFormat().getType();

        /* =====================================================
           TYPE-AWARE VALIDATION + STORAGE
           ===================================================== */

        switch (type) {

            case SCALE, NUMBER -> {

                Double value = extractNumeric(rawValue);

                session.getAnswerStore().put(
                        question.getId(),
                        responseKey,
                        value
                );
            }

            case TIME -> {

                LocalTime time = extractTime(rawValue);

                session.getAnswerStore().put(
                        question.getId(),
                        responseKey,
                        time.toString()
                );
            }

            default -> {

                // Future-safe fallback
                session.getAnswerStore().put(
                        question.getId(),
                        responseKey,
                        rawValue
                );
            }
        }

        /* =====================================================
           ADVANCE EXACTLY ONCE
           ===================================================== */

        session.incrementQuestionIndex();

        log.info(
                "PSQI after submit: index={}",
                session.getCurrentQuestionIndex()
        );
    }

    @Override
    public boolean isCompleted(QuestionnaireSession session) {

        boolean completed =
                session.getCurrentQuestionIndex()
                        >= getDefinition().getQuestions().size();

        if (completed && !session.isCompleted()) {
            session.setCompleted(true);
        }

        return completed;
    }

    /* =====================================================
       HELPERS
       ===================================================== */

    private Double extractNumeric(Object value) {

        if (value instanceof Number n) {
            return n.doubleValue();
        }

        throw new IllegalArgumentException(
                "Numeric response expected"
        );
    }

    private LocalTime extractTime(Object value) {

        try {

            if (value instanceof String s) {
                return LocalTime.parse(s);
            }

            if (value instanceof LocalTime t) {
                return t;
            }

        } catch (Exception ignored) {}

        throw new IllegalArgumentException(
                "Time response must be in HH:mm format"
        );
    }
}
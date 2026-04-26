package com.example.mentalhealth.questionnaire.implementation;

import com.example.mentalhealth.answers.AnswerSubmissionDTO;
import com.example.mentalhealth.questionnaire.DTO.QuestionDTO;
import com.example.mentalhealth.questionnaire.DTO.QuestionnaireDTO;
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
public class LSASQuestionnaireService implements QuestionnaireService {

    private final QuestionnaireRegistry questionnaireRegistry;

    @Override
    public String supportedQuestionnaireId() {
        return "lsas";
    }

    private QuestionnaireDTO getDefinition() {
        return questionnaireRegistry
                .getById(supportedQuestionnaireId())
                .orElseThrow(() ->
                        new IllegalStateException("LSAS questionnaire not found")
                );
    }

    @Override
    public QuestionDTO getNextQuestion(QuestionnaireSession session) {

        QuestionnaireDTO questionnaire = getDefinition();

        int index = session.getCurrentQuestionIndex();

        if (index >= questionnaire.getQuestions().size()) {
            throw new IllegalStateException("LSAS questionnaire already completed");
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
                    "questionnaire_id is required"
            );
        }

        if (!supportedQuestionnaireId().equals(submission.getQuestionnaireId())) {
            throw new IllegalArgumentException(
                    "Invalid questionnaire_id for LSAS: "
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
                    "LSAS session already completed"
            );
        }

        QuestionDTO question = getNextQuestion(session);

        log.info(
                "LSAS submit: session={}, incomingQuestion={}, expectedQuestion={}, index={}",
                submission.getSessionId(),
                submission.getQuestionId(),
                question.getId(),
                session.getCurrentQuestionIndex()
        );

        /* =====================================================
           STRICT QUESTION ORDER
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
           RESPONSE EXTRACTION
           ===================================================== */

        Map<String, Object> responses = submission.getResponses();

        if (responses == null) {
            throw new IllegalArgumentException(
                    "LSAS requires responses map"
            );
        }

        Map<String, String> responseKeys =
                question.getResponseFormat().getResponseKeys();

        if (responseKeys == null) {
            throw new IllegalStateException(
                    "LSAS response keys not configured correctly"
            );
        }

        if (!responseKeys.containsKey("fear")
                || !responseKeys.containsKey("avoidance")) {
            throw new IllegalStateException(
                    "LSAS logical response keys missing"
            );
        }

        if (!responses.containsKey("fear")
                || !responses.containsKey("avoidance")) {
            throw new IllegalArgumentException(
                    "LSAS requires both fear and avoidance responses"
            );
        }

        double fear = extractNumeric(responses.get("fear"));
        double avoidance = extractNumeric(responses.get("avoidance"));

        /* =====================================================
           SCALE VALIDATION
           ===================================================== */

        if (fear < 0 || fear > 3 || avoidance < 0 || avoidance > 3) {
            throw new IllegalArgumentException(
                    "LSAS fear and avoidance must be between 0 and 3"
            );
        }

        /* =====================================================
           STORE ANSWERS (SINGLE WRITER)
           ===================================================== */

        session.getAnswerStore().put(
                question.getId(),
                responseKeys.get("fear"),
                fear
        );

        session.getAnswerStore().put(
                question.getId(),
                responseKeys.get("avoidance"),
                avoidance
        );

        /* =====================================================
           ADVANCE EXACTLY ONCE
           ===================================================== */

        session.incrementQuestionIndex();

        log.info(
                "LSAS after submit: index={}",
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
       Helpers
       ===================================================== */

    private double extractNumeric(Object value) {

        if (value instanceof Number n) {
            return n.doubleValue();
        }

        throw new IllegalArgumentException(
                "LSAS response must be numeric"
        );
    }
}
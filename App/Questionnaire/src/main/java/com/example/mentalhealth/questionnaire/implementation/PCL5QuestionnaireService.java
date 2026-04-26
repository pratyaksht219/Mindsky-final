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

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PCL5QuestionnaireService implements QuestionnaireService {

    private final QuestionnaireRegistry questionnaireRegistry;

    @Override
    public String supportedQuestionnaireId() {
        return "pcl5";
    }

    private QuestionnaireDTO getDefinition() {
        return questionnaireRegistry
                .getById(supportedQuestionnaireId())
                .orElseThrow(() ->
                        new IllegalStateException("PCL-5 questionnaire not found")
                );
    }

    @Override
    public QuestionDTO getNextQuestion(QuestionnaireSession session) {
        QuestionnaireDTO questionnaire = getDefinition();

        int index = session.getCurrentQuestionIndex();
        if (index >= questionnaire.getQuestions().size()) {
            throw new IllegalStateException("PCL-5 questionnaire already completed");
        }

        return questionnaire.getQuestions().get(index);
    }

    @Override
    public void submitAnswer(
            AnswerSubmissionDTO submission,
            QuestionnaireSession session
    ) {

        /* =====================================================
            SESSION + QUESTIONNAIRE INTEGRITY (CRITICAL)
           ===================================================== */

        if (submission.getQuestionnaireId() == null) {
            throw new IllegalArgumentException(
                    "questionnaire_id is required in AnswerSubmissionDTO"
            );
        }

        if (!supportedQuestionnaireId().equals(submission.getQuestionnaireId())) {
            throw new IllegalArgumentException(
                    "Invalid questionnaire_id for PCL-5: "
                            + submission.getQuestionnaireId()
            );
        }

        if (!session.getQuestionnaireId().equals(supportedQuestionnaireId())) {
            throw new IllegalStateException(
                    "Session questionnaire mismatch. Session="
                            + session.getQuestionnaireId()
                            + ", Request="
                            + submission.getQuestionnaireId()
            );
        }

        if (!session.getSessionId().equals(submission.getSessionId())) {
            throw new IllegalStateException(
                    "Session ID mismatch. Expected "
                            + session.getSessionId()
                            + " but got "
                            + submission.getSessionId()
            );
        }

        if (session.isCompleted()) {
            throw new IllegalStateException(
                    "PCL-5 session already completed"
            );
        }

        QuestionDTO question = getNextQuestion(session);

        log.info(
                "PCL-5 submit: session={}, incomingQuestion={}, expectedQuestion={}, index={}",
                submission.getSessionId(),
                submission.getQuestionId(),
                question.getId(),
                session.getCurrentQuestionIndex()
        );

        /* =====================================================
           1️⃣ STRICT QUESTION ORDER ENFORCEMENT
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
   2️⃣ RESPONSE TYPE + SCALE GUARD
   ===================================================== */

        if (question.getResponseFormat().getType() != ResponseType.SCALE) {
            throw new IllegalStateException(
                    "PCL-5 only supports SCALE responses"
            );
        }

        Double value;

        /* Preferred path: simple numeric answer */
        if (submission.getAnswer() != null) {

            value = submission.getAnswer().doubleValue();

        }
        /* Fallback path: responses map */
        else {

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

            value = extractNumeric(responses.get(responseKey));
        }

        /* PCL-5 scale: 0–4 */
        if (value < 0 || value > 4) {
            throw new IllegalArgumentException(
                    "PCL-5 response must be between 0 and 4"
            );
        }

        /* =====================================================
           3️⃣ STORE (SINGLE WRITER)
           ===================================================== */

        String storeKey = question.getResponseFormat().getResponseKey();

        session.getAnswerStore().put(
                question.getId(),
                storeKey,
                value
        );

        /* =====================================================
           4️⃣ ADVANCE EXACTLY ONCE
           ===================================================== */

        session.incrementQuestionIndex();

        log.info(
                "PCL-5 after submit: index={}",
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

    private Double extractNumeric(Object value) {
        if (value instanceof Number n) {
            return n.doubleValue();
        }
        throw new IllegalArgumentException(
                "PCL-5 response must be numeric"
        );
    }
}
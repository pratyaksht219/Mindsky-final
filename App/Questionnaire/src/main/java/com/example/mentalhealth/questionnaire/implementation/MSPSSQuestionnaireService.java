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

@Slf4j
@Service
@RequiredArgsConstructor
public class MSPSSQuestionnaireService implements QuestionnaireService {

    private final QuestionnaireRegistry questionnaireRegistry;

    @Override
    public String supportedQuestionnaireId() {
        return "mspss";
    }

    private QuestionnaireDTO getDefinition() {
        return questionnaireRegistry
                .getById(supportedQuestionnaireId())
                .orElseThrow(() ->
                        new IllegalStateException("MSPSS questionnaire not found")
                );
    }

    @Override
    public QuestionDTO getNextQuestion(QuestionnaireSession session) {
        QuestionnaireDTO questionnaire = getDefinition();

        int index = session.getCurrentQuestionIndex();
        if (index >= questionnaire.getQuestions().size()) {
            throw new IllegalStateException("MSPSS questionnaire already completed");
        }

        return questionnaire.getQuestions().get(index);
    }

    @Override
    public void submitAnswer(
            AnswerSubmissionDTO submission,
            QuestionnaireSession session
    ) {
        if (!supportedQuestionnaireId().equals(submission.getQuestionnaireId())) {
            throw new IllegalArgumentException(
                    "Invalid questionnaire_id for MSPSS: "
                            + submission.getQuestionnaireId()
            );
        }

        QuestionDTO question = getNextQuestion(session);

        if (!question.getId().equals(submission.getQuestionId())) {
            throw new IllegalArgumentException(
                    "Question ID mismatch. Expected "
                            + question.getId()
                            + " but got "
                            + submission.getQuestionId()
            );
        }

        if (question.getResponseFormat().getType() != ResponseType.SCALE) {
            throw new IllegalStateException(
                    "MSPSS only supports SCALE responses"
            );
        }

        Double value = AnswerExtractor.extractScaleValue(submission, question);

        if (value < 1 || value > 7) {
            throw new IllegalArgumentException(
                    "MSPSS response must be between 1 and 7"
            );
        }

        session.getAnswerStore().put(
                question.getId(),
                question.getResponseFormat().getResponseKey(),
                value
        );

        session.incrementQuestionIndex();
    }

    @Override
    public boolean isCompleted(QuestionnaireSession session) {
        return session.getCurrentQuestionIndex()
                >= getDefinition().getQuestions().size();
    }
}

package com.example.mentalhealth.questionnaire;

import com.example.mentalhealth.answers.AnswerSubmissionDTO;
import com.example.mentalhealth.session.QuestionnaireSession;
import com.example.mentalhealth.questionnaire.DTO.QuestionDTO;

public interface QuestionnaireService {

    String supportedQuestionnaireId();

    QuestionDTO getNextQuestion(QuestionnaireSession session);

    void submitAnswer(
            AnswerSubmissionDTO submission,
            QuestionnaireSession session
    );

    boolean isCompleted(QuestionnaireSession session);

}
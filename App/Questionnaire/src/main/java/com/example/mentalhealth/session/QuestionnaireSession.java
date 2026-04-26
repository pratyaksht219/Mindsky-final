package com.example.mentalhealth.session;

import com.example.mentalhealth.answers.AnswerStore;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionnaireSession {
    private final String sessionId;
    private final String questionnaireId;
    private int currentQuestionIndex = 0;
    private final AnswerStore answerStore;
    boolean completed = false;

    public QuestionnaireSession(String sessionId, String questionnaireId, AnswerStore answerStore) {
        this.sessionId = sessionId;
        this.questionnaireId = questionnaireId;
        this.answerStore = answerStore;
    }
    public void incrementQuestionIndex() {
        currentQuestionIndex++;
    }


}
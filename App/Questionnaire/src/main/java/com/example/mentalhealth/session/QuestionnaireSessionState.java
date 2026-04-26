package com.example.mentalhealth.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireSessionState {

    private static final long serialVersionUID = 1L;

    private String questionnaireId;
    private int currentQuestionIndex = 0;
    private List<Integer> answers = new ArrayList<>();
    private List<Integer> secondaryAnswers = new ArrayList<>();
    private boolean completed = false;


}
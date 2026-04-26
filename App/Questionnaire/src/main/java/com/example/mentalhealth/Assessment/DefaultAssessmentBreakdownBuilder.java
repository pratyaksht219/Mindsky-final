package com.example.mentalhealth.Assessment;

import com.example.mentalhealth.answers.AnswerStore;
import com.example.mentalhealth.dto.*;
import com.example.mentalhealth.questionnaire.DTO.QuestionDTO;
import com.example.mentalhealth.questionnaire.DTO.QuestionnaireDTO;
import com.example.mentalhealth.scoring.DTO.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DefaultAssessmentBreakdownBuilder
        implements AssessmentBreakdownBuilder {

    @Override
    public AssessmentBreakdown build(
            QuestionnaireDTO questionnaire,
            ScoringDTO scoring,
            AnswerStore answers,
            AssessmentResult result
    ) {

        /* ===============================
           1️⃣ QUESTION-LEVEL BREAKDOWN
           =============================== */

        List<QuestionResponseBreakdown> questionBreakdowns = new ArrayList<>();

        for (QuestionDTO question : questionnaire.getQuestions()) {

            Map<String, Object> questionAnswers =
                    answers.getAllForQuestion(question.getId());

            List<ResponseValueBreakdown> values =
                    questionAnswers.entrySet()
                            .stream()
                            .map(e -> {
                                Object raw = e.getValue();

                                Double normalized =
                                        raw instanceof Number
                                                ? ((Number) raw).doubleValue()
                                                : null;

                                return new ResponseValueBreakdown(
                                        e.getKey(),
                                        raw,
                                        normalized,
                                        "identity",
                                        null
                                );
                            })
                            .toList();

            questionBreakdowns.add(
                    new QuestionResponseBreakdown(
                            question.getId(),
                            question.getText(),
                            values
                    )
            );
        }

        /* ===============================
   2. COMPONENT BREAKDOWN (READ-ONLY)
   =============================== */

        List<ComponentBreakdown> componentBreakdowns = new ArrayList<>();

        for (ComponentDTO component : scoring.getComponents()) {

            List<ComponentInputBreakdown> inputs =
                    component.getCalculation()
                            .getInputs()
                            .stream()
                            .map(i -> {

                                Optional<Object> rawOpt =
                                        answers.get(i.getQuestionId(), i.getResponseKey());
                                Object raw = rawOpt.orElse(null);

                                return new ComponentInputBreakdown(
                                    i.getResponseKey(),
                                    i.getQuestionId(),
                                        raw,
                                    answers.getDouble(
                                            i.getQuestionId(),
                                            i.getResponseKey()
                                    ).orElse(null)
                            );}
                            )
                            .toList();

            Double score =
                    result.getComponentScores() != null
                            ? result.getComponentScores().get(component.getId())
                            : null;

            componentBreakdowns.add(
                    new ComponentBreakdown(
                            component.getId(),
                            component.getName(),
                            inputs,
                            "engine",
                            score
                    )
            );
        }

        /* ===============================
           3️⃣ GLOBAL SCORE (SOURCE OF TRUTH)
           =============================== */

        GlobalScoreBreakdown global =
                new GlobalScoreBreakdown(
                        result.getFinalScore(),
                        result.getSeverityLabel(),
                        result.getDescription(),
                        scoring.getGlobalScore()
                                .getCalculation()
                                .getComponents()
                );

        return new AssessmentBreakdown(
                scoring.getQuestionnaireId(),
                questionBreakdowns,
                componentBreakdowns,
                global,
                Map.of("engine", "scoring-engine")
        );
    }
}
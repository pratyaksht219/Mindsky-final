package com.example.mentalhealth.scoring.FormulaEngines;

import com.example.mentalhealth.answers.AnswerStore;

import java.util.List;

public final class DASS21FormulaEngine {

    private DASS21FormulaEngine() {
        // utility class
    }

    /* ===============================
       Public API (called by scoring)
       =============================== */

    public static Double depressionScore(AnswerStore answers) {
        return scaledSum(answers, DEPRESSION_ITEMS);
    }

    public static Double anxietyScore(AnswerStore answers) {
        return scaledSum(answers, ANXIETY_ITEMS);
    }

    public static Double stressScore(AnswerStore answers) {
        return scaledSum(answers, STRESS_ITEMS);
    }

    /* ===============================
       Core Logic
       =============================== */

    private static Double scaledSum(
            AnswerStore answers,
            List<String> questionIds
    ) {
        double rawSum = 0.0;

        for (String qid : questionIds) {
            rawSum += answers
                    .getDouble(qid, qid)
                    .orElse(0.0);
        }

        // DASS-21 scaling rule
        return rawSum * 2;
    }

    /* ===============================
       Item mappings (WHO standard)
       =============================== */

    private static final List<String> DEPRESSION_ITEMS = List.of(
            "dass21_q3",
            "dass21_q5",
            "dass21_q10",
            "dass21_q13",
            "dass21_q16",
            "dass21_q17",
            "dass21_q21"
    );

    private static final List<String> ANXIETY_ITEMS = List.of(
            "dass21_q2",
            "dass21_q4",
            "dass21_q7",
            "dass21_q9",
            "dass21_q15",
            "dass21_q19",
            "dass21_q20"
    );

    private static final List<String> STRESS_ITEMS = List.of(
            "dass21_q1",
            "dass21_q6",
            "dass21_q8",
            "dass21_q11",
            "dass21_q12",
            "dass21_q14",
            "dass21_q18"
    );
}
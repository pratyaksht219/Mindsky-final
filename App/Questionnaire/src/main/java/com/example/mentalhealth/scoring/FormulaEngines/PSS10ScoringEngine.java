package com.example.mentalhealth.scoring.FormulaEngines;

import com.example.mentalhealth.answers.AnswerStore;

import java.util.Set;

public final class PSS10ScoringEngine {

    private PSS10ScoringEngine() {}

    /* PSS-10 reverse-scored items (clinical standard) */
    private static final Set<Integer> REVERSE_ITEMS =
            Set.of(4, 5, 7, 8);

    /**
     * Computes final PSS-10 score.
     *
     * Scale: 0–4
     * Reverse scoring: 4 → 0, 3 → 1, 2 → 2, 1 → 3, 0 → 4
     */
    public static double compute(AnswerStore answers) {

        double total = 0;

        for (int i = 1; i <= 10; i++) {

            String questionId = "pss_q" + i;
            String responseKey = "value";

            double raw =
                    answers.getDouble(questionId, responseKey)
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Missing PSS-10 response for "
                                                    + questionId
                                    ));

            double scored =
                    REVERSE_ITEMS.contains(i)
                            ? reverse(raw)
                            : raw;

            total += scored;
        }

        return total;
    }

    private static double reverse(double value) {
        if (value < 0 || value > 4) {
            throw new IllegalArgumentException(
                    "Invalid PSS-10 value for reverse scoring: " + value
            );
        }
        return 4 - value;
    }
}
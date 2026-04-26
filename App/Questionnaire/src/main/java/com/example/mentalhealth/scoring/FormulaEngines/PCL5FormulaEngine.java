package com.example.mentalhealth.scoring.FormulaEngines;

import com.example.mentalhealth.answers.AnswerStore;

import java.util.List;

public final class PCL5FormulaEngine {

    private PCL5FormulaEngine() {}

    /* =====================================================
       TOTAL SEVERITY SCORE (0–80)
       ===================================================== */
    public static double totalSeverity(AnswerStore answers) {
        return answers.getAll()
                .values()
                .stream()
                .flatMap(m -> m.values().stream())
                .filter(v -> v instanceof Number)
                .mapToDouble(v -> ((Number) v).doubleValue())
                .sum();
    }

    /* =====================================================
        CRITERION SCORES
       ===================================================== */

    public static double criterionBIntrusionScore(AnswerStore answers) {
        return sum(
                answers,
                List.of("pcl5_q1", "pcl5_q2", "pcl5_q3", "pcl5_q4", "pcl5_q5")
        );
    }

    public static double criterionCAvoidanceScore(AnswerStore answers) {
        return sum(
                answers,
                List.of("pcl5_q6", "pcl5_q7")
        );
    }

    public static double criterionDCognitionMoodScore(AnswerStore answers) {
        return sum(
                answers,
                List.of(
                        "pcl5_q8", "pcl5_q9", "pcl5_q10",
                        "pcl5_q11", "pcl5_q12", "pcl5_q13", "pcl5_q14"
                )
        );
    }

    public static double criterionEArousalScore(AnswerStore answers) {
        return sum(
                answers,
                List.of(
                        "pcl5_q15", "pcl5_q16", "pcl5_q17",
                        "pcl5_q18", "pcl5_q19", "pcl5_q20"
                )
        );
    }

    /* =====================================================
      DSM-5 CRITERIA CHECKS
       ===================================================== */

    public static boolean meetsCriterionB(AnswerStore answers) {
        return countAtLeast(
                answers,
                List.of("pcl5_q1", "pcl5_q2", "pcl5_q3", "pcl5_q4", "pcl5_q5"),
                2
        ) >= 1;
    }

    public static boolean meetsCriterionC(AnswerStore answers) {
        return countAtLeast(
                answers,
                List.of("pcl5_q6", "pcl5_q7"),
                2
        ) >= 1;
    }

    public static boolean meetsCriterionD(AnswerStore answers) {
        return countAtLeast(
                answers,
                List.of(
                        "pcl5_q8", "pcl5_q9", "pcl5_q10",
                        "pcl5_q11", "pcl5_q12", "pcl5_q13", "pcl5_q14"
                ),
                2
        ) >= 2;
    }

    public static boolean meetsCriterionE(AnswerStore answers) {
        return countAtLeast(
                answers,
                List.of(
                        "pcl5_q15", "pcl5_q16", "pcl5_q17",
                        "pcl5_q18", "pcl5_q19", "pcl5_q20"
                ),
                2
        ) >= 2;
    }

    /* =====================================================
        PROVISIONAL PTSD DIAGNOSIS
       ===================================================== */
    public static boolean hasProvisionalPTSD(AnswerStore answers) {
        return meetsCriterionB(answers)
                && meetsCriterionC(answers)
                && meetsCriterionD(answers)
                && meetsCriterionE(answers);
    }

    /* =====================================================
        Helpers
       ===================================================== */

    private static double sum(
            AnswerStore answers,
            List<String> questionIds
    ) {
        return questionIds.stream()
                .mapToDouble(q ->
                        answers.getDouble(q, "value").orElse(0.0)
                )
                .sum();
    }

    private static long countAtLeast(
            AnswerStore answers,
            List<String> questionIds,
            double threshold
    ) {
        return questionIds.stream()
                .map(q ->
                        answers.getDouble(q, "value").orElse(0.0)
                )
                .filter(v -> v >= threshold)
                .count();
    }
}
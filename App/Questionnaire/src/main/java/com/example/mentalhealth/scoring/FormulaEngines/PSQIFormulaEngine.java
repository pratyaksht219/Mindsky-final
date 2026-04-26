package com.example.mentalhealth.scoring.FormulaEngines;

import com.example.mentalhealth.answers.AnswerStore;

import java.time.Duration;
import java.time.LocalTime;

public final class PSQIFormulaEngine {

    private PSQIFormulaEngine() {}

    /* ===========================
       COMPONENT CALCULATIONS
       =========================== */

    public static int subjectiveSleepQuality(AnswerStore a) {
        return a.getInt("psqi_q6", "value").orElse(0);
    }

    public static int sleepLatency(AnswerStore a) {

        int latencyMin =
                a.getInt("psqi_q2", "minutes").orElse(0);

        int q5a =
                a.getInt("psqi_q5a", "value").orElse(0);

        int latencyScore =
                latencyMin <= 15 ? 0 :
                        latencyMin <= 30 ? 1 :
                                latencyMin <= 60 ? 2 : 3;

        int sum = latencyScore + q5a;

        return sum == 0 ? 0 :
                sum <= 2 ? 1 :
                        sum <= 4 ? 2 : 3;
    }

    public static int sleepDuration(AnswerStore a) {

        double hours =
                a.getDouble("psqi_q4", "hours").orElse(0.0);

        return hours >= 7 ? 0 :
                hours >= 6 ? 1 :
                        hours >= 5 ? 2 : 3;
    }

    public static int habitualSleepEfficiency(AnswerStore a) {

        LocalTime bed =
                a.getString("psqi_q1", "time")
                        .map(LocalTime::parse)
                        .orElse(LocalTime.MIDNIGHT);

        LocalTime wake =
                a.getString("psqi_q3", "time")
                        .map(LocalTime::parse)
                        .orElse(LocalTime.MIDNIGHT);

        double sleepHours =
                a.getDouble("psqi_q4", "hours").orElse(0.0);

        long minutesInBed =
                Duration.between(bed, wake).toMinutes();

        if (minutesInBed <= 0) {
            minutesInBed += 24 * 60;
        }

        double efficiency =
                (sleepHours * 60) / minutesInBed * 100;

        return efficiency >= 85 ? 0 :
                efficiency >= 75 ? 1 :
                        efficiency >= 65 ? 2 : 3;
    }

    public static int sleepDisturbances(AnswerStore a) {

        int sum = 0;

        for (char c = 'b'; c <= 'i'; c++) {
            sum += a.getInt("psqi_q5" + c, "value").orElse(0);
        }

        return sum == 0 ? 0 :
                sum <= 9 ? 1 :
                        sum <= 18 ? 2 : 3;
    }

    public static int sleepingMedication(AnswerStore a) {
        return a.getInt("psqi_q7", "value").orElse(0);
    }

    public static int daytimeDysfunction(AnswerStore a) {

        int sum =
                a.getInt("psqi_q8", "value").orElse(0)
                        + a.getInt("psqi_q9", "value").orElse(0);

        return sum == 0 ? 0 :
                sum <= 2 ? 1 :
                        sum <= 4 ? 2 : 3;
    }

    /* ===========================
       GLOBAL SCORE
       =========================== */

    public static int globalScore(AnswerStore a) {
        return subjectiveSleepQuality(a)
                + sleepLatency(a)
                + sleepDuration(a)
                + habitualSleepEfficiency(a)
                + sleepDisturbances(a)
                + sleepingMedication(a)
                + daytimeDysfunction(a);
    }
}
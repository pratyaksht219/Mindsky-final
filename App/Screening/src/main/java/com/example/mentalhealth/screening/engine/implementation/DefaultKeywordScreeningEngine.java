package com.example.mentalhealth.screening.engine.implementation;

import com.example.mentalhealth.screening.domain.*;
import com.example.mentalhealth.screening.engine.KeywordScreeningEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class DefaultKeywordScreeningEngine implements KeywordScreeningEngine {

    private static final double ROUTING_THRESHOLD = 4.0;

    private static final int MIN_SCREENING_TURNS = 3;

    private static final int MAX_SCREENING_TURNS = 6;

    private static final String FALLBACK_QUESTIONNAIRE = "dass21";

    private static final Map<String, String> DOMAIN_TO_QUESTIONNAIRE =
            Map.of(
                    "depression", "phq9",
                    "anxiety", "gad7",
                    "adhd", "asrs",
                    "sleep", "psqi",
                    "stress", "pss10",
                    "social_anxiety", "lsas",
                    "trauma", "pcl5",
                    "distress_general","k10",
                    "social_support", "mspss",
                    "vague", "dass21"
            );

    @Override
    public ScreeningDecision evaluate(
            ScreeningSignalState signalState,
            EmergencyMatch emergencyMatch
    ) {

        log.debug("Evaluating screening decision | turns={}", signalState.getTurnCount());

        /* =========================
           1️⃣ Emergency short-circuit
           ========================= */

        if (emergencyMatch.getLevel() == EmergencyLevel.HIGH) {

            log.warn("Emergency signal detected during screening");

            return new ScreeningDecision(
                    true,
                    null,
                    1.0,
                    List.of(emergencyMatch.getMatchedPhrase()),
                    true
            );
        }

        /* =========================
           2️⃣ Enforce minimum turns
           ========================= */

        if (signalState.getTurnCount() < MIN_SCREENING_TURNS) {

            log.debug("Minimum screening turns not reached");

            return notReady();
        }

        /* =========================
           3️⃣ Find dominant signal
           ========================= */

        Optional<Map.Entry<String, Double>> dominant =
                signalState.getDominantDomain();

        if (dominant.isPresent()) {
            String domain = dominant.get().getKey().toLowerCase();
            double score = dominant.get().getValue();
            log.debug("Dominant domain={} score={}", domain, score);

            /* =========================
               4️⃣ Check threshold
               ========================= */

            if (score >= ROUTING_THRESHOLD) {

                String questionnaireId =
                        DOMAIN_TO_QUESTIONNAIRE.get(domain);

                if (questionnaireId == null) {
                    questionnaireId = FALLBACK_QUESTIONNAIRE;
                }

                double confidence =
                        Math.min(1.0, score / 5.0);

                log.info(
                        "Routing screening → {} | confidence={}",
                        questionnaireId,
                        confidence
                );

                return new ScreeningDecision(
                        true,
                        questionnaireId,
                        confidence,
                        signalState.getMatchedKeywords(domain),
                        false
                );
            }
        }

        /* =========================
           5️⃣ Max turns fallback
           ========================= */

        if (signalState.getTurnCount() >= MAX_SCREENING_TURNS) {

            log.info("Max turns reached → fallback questionnaire");

            return new ScreeningDecision(
                    true,
                    FALLBACK_QUESTIONNAIRE,
                    0.5,
                    List.of("insufficient signal"),
                    false
            );
        }

        return notReady();
    }

    private ScreeningDecision notReady() {
        return new ScreeningDecision(
                false,
                null,
                0.0,
                List.of(),
                false
        );
    }
}
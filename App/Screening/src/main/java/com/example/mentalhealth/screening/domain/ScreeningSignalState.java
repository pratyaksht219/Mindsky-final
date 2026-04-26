package com.example.mentalhealth.screening.domain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.*;

@Slf4j
@Getter
public class ScreeningSignalState implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, Double> microSignalScores = new HashMap<>();

    private Map<String, Double> domainSignalScores = new HashMap<>();

    private Set<String> matchedKeywords = new HashSet<>();

    private Set<String> matchedMicroSignals = new HashSet<>();

    private EmergencyLevel emergencyLevel = EmergencyLevel.NONE;

    private int turnCount = 0;

    /* =========================
       SIGNAL ACCUMULATION
       ========================= */

    public void addMicroSignal(
            String microSignal,
            String domain,
            double weight
    ) {
        microSignalScores.merge(microSignal, weight, Double::sum);
        domainSignalScores.merge(domain, weight, Double::sum);
        matchedMicroSignals.add(microSignal);
    }

    public void addKeyword(String keyword) {
        matchedKeywords.add(keyword);
    }

    /* =========================
       EMERGENCY
       ========================= */

    public void escalateEmergency(EmergencyLevel newLevel) {
        if (newLevel.ordinal() > this.emergencyLevel.ordinal()) {
            this.emergencyLevel = newLevel;
        }
    }

    public boolean isEmergency() {
        return emergencyLevel == EmergencyLevel.HIGH;
    }

    /* =========================
       TURN MANAGEMENT
       ========================= */

    public void incrementTurn() {
        turnCount++;
    }

    /* =========================
       DOMAIN HELPERS
       ========================= */

    public Optional<Map.Entry<String, Double>> getDominantDomain() {
        return domainSignalScores
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());
    }

    public double getDominantScore() {
        return getDominantDomain()
                .map(Map.Entry::getValue)
                .orElse(0.0);
    }

    public List<String> getMatchedKeywords(String domain) {
        return new ArrayList<>(matchedKeywords);
    }

    public void addMLSignal(String domain, double similarity) {

        double SIGNAL_THRESHOLD = 0.45;

        if(similarity < SIGNAL_THRESHOLD) {
            return;
        }

        double TURN_WEIGHT = 3.0;

        double weightedScore = similarity * TURN_WEIGHT;

        log.info(
                "Accumulating ML signal | domain={} | similarity={} | weightedScore={}",
                domain,
                similarity,
                weightedScore
        );


        domainSignalScores.merge(
                domain,
                weightedScore,
                Double::sum
        );

        log.info(
                "Updated accumulated domain score | domain={} | totalScore={}",
                domain,
                domainSignalScores.get(domain)
        );
    }

    public void reset() {
        microSignalScores.clear();
        domainSignalScores.clear();
        matchedKeywords.clear();
        matchedMicroSignals.clear();
        emergencyLevel = EmergencyLevel.NONE;
        turnCount = 0;
    }
}
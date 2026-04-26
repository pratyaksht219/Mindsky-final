package com.example.mentalhealth.screening.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class DomainAggregationResult {

    private String dominantDomain;

    private double confidence;

    private Map<String, Double> normalizedScores;

    private boolean conflictDetected;
}
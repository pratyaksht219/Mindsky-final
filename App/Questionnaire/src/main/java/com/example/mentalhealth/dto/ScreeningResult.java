package com.example.mentalhealth.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Final outcome of Phase-0 screening.
 * Immutable decision snapshot.
 */
@Data
@Builder
public class ScreeningResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Whether screening completed successfully.
     */
    private boolean completed;

    /**
     * Whether crisis was detected.
     * If true → no questionnaire should be started.
     */
    private boolean crisis;

    /**
     * Primary questionnaire to start (if not crisis).
     */
    private String questionnaireId;

    /**
     * Confidence score of routing decision (0.0 – 1.0).
     */
    private double confidence;

    /**
     * Matched signals or keywords that influenced decision.
     * Used for explainability and audit.
     */
    private List<String> rationale;

    /**
     * Optional emergency severity label (LOW, MODERATE, HIGH).
     */
    private String emergencyLevel;
}
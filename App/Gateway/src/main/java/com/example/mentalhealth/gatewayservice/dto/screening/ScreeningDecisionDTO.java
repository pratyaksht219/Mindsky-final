package com.example.mentalhealth.gatewayservice.dto.screening;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreeningDecisionDTO {

    /** Selected questionnaire */
    private String questionnaireId;

    /** Confidence score (0.0 – 1.0) */
    private double confidence;

    /** Crisis detected */
    private boolean emergency;

    /** Signals used for explainability */
    private List<String> matchedSignals;
}
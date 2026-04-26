package com.example.mentalhealth.screening.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreeningDecision {

    /** decision ready? */
    private boolean ready;

    /** selected questionnaire */
    private String questionnaireId;

    /** confidence score */
    private double confidence;

    /** explainability */
    private List<String> matchedSignals;

    /** crisis detected */
    private boolean emergency;

}
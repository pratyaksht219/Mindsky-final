package com.example.mentalhealth.Assessment;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartAssessmentRequest {

    /**
     * Questionnaire to start
     * Examples: gad7, phq9, lsas, psqi
     */
    private String questionnaireId;

    /**
     * Optional: future-proofing
     * Could be used for locale, clinical mode, etc.
     */
    private String context;
}

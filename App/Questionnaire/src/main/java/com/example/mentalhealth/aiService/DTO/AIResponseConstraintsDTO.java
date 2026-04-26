package com.example.mentalhealth.aiService.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIResponseConstraintsDTO {

    private boolean allowDiagnosis;      // ALWAYS false
    private boolean allowTreatmentAdvice; // false or soft
    private boolean requireDisclaimer;    // true
    private int maxResponseLength;        // safety
}
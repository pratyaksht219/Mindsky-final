package com.example.mentalhealth.aiService.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RiskSignalDTO {

    private String signalId;           // suicide_risk_flag
    private String level;              // NONE | LOW | MODERATE | HIGH
    private String sourceComponentId;  // phq9_q9
    private String clinicalNote;       // short factual statement
}
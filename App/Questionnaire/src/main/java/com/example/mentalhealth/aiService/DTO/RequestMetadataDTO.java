package com.example.mentalhealth.aiService.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestMetadataDTO {

    private String requestId;              // traceability
    private String questionnaireId;         // psqi, phq9, gad7, etc.
    private String questionnaireName;       // optional, human readable
    private String language;                // "en", future i18n
    private Instant createdAt;
}
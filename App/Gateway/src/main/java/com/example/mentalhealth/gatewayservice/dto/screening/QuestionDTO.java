package com.example.mentalhealth.gatewayservice.dto.screening;

import com.example.mentalhealth.gatewayservice.dto.questionnaire.ResponseFormatDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a screening question asked to the user.
 * This is intentionally lightweight and independent
 * from questionnaire-service DTOs.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {

    /** Unique identifier for the screening question */
    private String id;

    /** Text shown to the user */
    private String text;

    /** Optional domain classification (depression, anxiety, sleep etc) */
    private String domain;

    @JsonProperty("response_format")
    private ResponseFormatDTO responseFormat;
}
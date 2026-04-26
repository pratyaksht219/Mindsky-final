package com.example.mentalhealth.gatewayservice.dto.questionnaire;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseFormatDTO {
    private ResponseType type; // Enum: scale, dual_scale, number, time, boolean, text

    @JsonProperty("response_key")
    private String responseKey; // Required for most types

    private String scale; // Required for single 'scale' type

    @JsonProperty("response_keys")
    private Map<String, String> responseKeys; // Required for 'dual_scale' type

    private String unit;
    private Double min;
    private Double max;
    private Double step;
    @JsonProperty("allow_decimal")
    private Boolean allowDecimal;
}
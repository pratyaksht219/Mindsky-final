package com.example.mentalhealth.questionnaire.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ResponseType {
    @JsonProperty("scale") SCALE,
    @JsonProperty("dual_scale") DUAL_SCALE,
    @JsonProperty("number") NUMBER,
    @JsonProperty("time") TIME,
    @JsonProperty("boolean") BOOLEAN,
    @JsonProperty("text") TEXT
}

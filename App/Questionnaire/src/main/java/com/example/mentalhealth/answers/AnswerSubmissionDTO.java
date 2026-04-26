package com.example.mentalhealth.answers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerSubmissionDTO {

    /** Questionnaire ID (e.g., "lsas", "psqi") */
    private String questionnaireId;
    private String sessionId;
    /** Question being answered */
    private String questionId;

    private Integer answer;
    /**
     * Key-value map of response_key → value
     *
     * Examples:
     *  scale:       { "phq9_q1": 2 }
     *  dual_scale:  { "lsas_q3_fear": 2, "lsas_q3_avoidance": 1 }
     *  number:      { "psqi_sleep_latency_minutes": 45 }
     */
    @JsonProperty("responses")
    private Map<String, Object> responses;
}
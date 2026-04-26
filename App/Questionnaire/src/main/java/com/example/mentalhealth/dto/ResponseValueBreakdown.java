package com.example.mentalhealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseValueBreakdown {

    /** response_key (fear, avoidance, q1_score, etc.) */
    private String responseKey;

    /** Raw user input */
    private Object rawValue;

    /** Value after transform (bucket / identity / formula) */
    private Double normalizedValue;

    /** Transform applied (identity, bucket, etc.) */
    private String transformType;

    /** Optional explanation */
    private String note;
}
package com.example.mentalhealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComponentInputBreakdown {

    private String responseKey;
    private String questionId;
    private Object rawValue;
    private Double normalizedValue;
}
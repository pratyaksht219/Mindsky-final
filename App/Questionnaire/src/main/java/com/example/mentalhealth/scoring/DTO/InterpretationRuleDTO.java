package com.example.mentalhealth.scoring.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterpretationRuleDTO {
    private Double min;
    private Double max;
    private String label;
    private String description;
}

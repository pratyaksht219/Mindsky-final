package com.example.mentalhealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComponentBreakdown {

    private String componentId;
    private String componentName;

    /** Inputs used */
    private List<ComponentInputBreakdown> inputs;

    /** Calculation type (sum, average, formula) */
    private String calculationType;

    /** Resulting score */
    private Double score;
}
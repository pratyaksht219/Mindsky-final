package com.example.mentalhealth.scoring.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalCalculationDTO {
    private String type; // sum_components, formula
    private List<String> components;
    private String formula;
    @JsonProperty("score_range")
    private List<Double> scoreRange;
}
package com.example.mentalhealth.scoring.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculationDTO {
    private String type; // sum, average, max, formula
    private List<InputDTO> inputs;
    private String formula;
    @JsonProperty("score_range")
    private List<Double> scoreRange; // Array of 2 numbers
}

package com.example.mentalhealth.scoring.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComponentDTO {
    private String id;
    private String name;
    private CalculationDTO calculation;
}

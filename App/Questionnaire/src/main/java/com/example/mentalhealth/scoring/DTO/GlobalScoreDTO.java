package com.example.mentalhealth.scoring.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalScoreDTO {
    private GlobalCalculationDTO calculation;
    private InterpretationDTO interpretation;
}

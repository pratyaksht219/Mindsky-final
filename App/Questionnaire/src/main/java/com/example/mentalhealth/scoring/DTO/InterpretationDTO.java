package com.example.mentalhealth.scoring.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterpretationDTO {
    private List<InterpretationRuleDTO> rules;
}

package com.example.mentalhealth.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalScoreBreakdown {

    private Double score;

    private String label;
    private String description;

    /** Which components contributed */
    private List<String> componentIds;
}
package com.example.mentalhealth.aiService.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.example.mentalhealth.util.Constants.DEFAULT_DISCLAIMER;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIServiceResponseDTO {


    private String disclaimer = DEFAULT_DISCLAIMER;

    private String summary;

    private String severityExplanation;

    private List<String> keyFindings;

    private List<String> recommendations;

    private String reassurance;
}
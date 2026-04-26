package com.example.mentalhealth.scoring.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoringDTO {
    private String id;
    @JsonProperty("questionnaire_id")
    private String questionnaireId;
    private String method;
    private List<ResponseMappingDTO> responses;
    private List<ComponentDTO> components;
    @JsonProperty("global_score")
    private GlobalScoreDTO globalScore;
}




















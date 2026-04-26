package com.example.mentalhealth.questionnaire.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {
    private String id;
    private String text;
    private String domain; // Optional classification
    @JsonProperty("response_format")
    private ResponseFormatDTO responseFormat;
}

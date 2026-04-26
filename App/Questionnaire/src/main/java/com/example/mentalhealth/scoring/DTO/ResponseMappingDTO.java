package com.example.mentalhealth.scoring.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMappingDTO {
    @JsonProperty("question_id")
    private String questionId;
    @JsonProperty("response_key")
    private String responseKey;
    private TransformDTO transform;
}

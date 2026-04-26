package com.example.mentalhealth.questionnaire.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionnaireDTO {
    private String id;
    private String name;
    @JsonProperty("short_name")
    private String shortName;
    private String category;
    private String description;
    private String version;
    private String source;
    private String instructions;
    private List<QuestionDTO> questions;
    private MetadataDTO metadata;
}








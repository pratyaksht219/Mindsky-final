package com.example.mentalhealth.screening.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreeningResult implements Serializable {

    /** primary questionnaire */
    private String questionnaireId;

    /** optional followups */
    private List<String> followUpQuestionnaires;

    /** rationale / explainability */
    private String rationale;

}
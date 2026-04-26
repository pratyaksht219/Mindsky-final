package com.example.mentalhealth.Assessment;

import com.example.mentalhealth.dto.ComponentBreakdown;
import com.example.mentalhealth.dto.GlobalScoreBreakdown;
import com.example.mentalhealth.dto.QuestionResponseBreakdown;
import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssessmentBreakdown {

    private String questionnaireId;

    /** Per-question response breakdown */
    private List<QuestionResponseBreakdown> responses;

    /** Per-component scoring breakdown */
    private List<ComponentBreakdown> components;

    /** Final computed score */
    private GlobalScoreBreakdown globalScore;

    /** Optional debug / trace metadata */
    private Map<String, Object> metadata;
}

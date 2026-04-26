package com.example.mentalhealth.aiService.BuilderImplementation;

import com.example.mentalhealth.Assessment.AssessmentBreakdown;
import com.example.mentalhealth.Assessment.AssessmentResult;
import com.example.mentalhealth.aiService.AIContextBuilder;
import com.example.mentalhealth.aiService.DTO.*;
import com.example.mentalhealth.dto.ComponentBreakdown;
import com.example.mentalhealth.dto.ComponentInputBreakdown;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Component
public class DefaultAIContextBuilder implements AIContextBuilder {

    @Override
    public AIServiceRequestDTO build(
            AssessmentResult result,
            AssessmentBreakdown breakdown
    ) {

        AIServiceRequestDTO dto = new AIServiceRequestDTO();

        dto.setMetadata(buildMetadata(result));
        dto.setAssessment(buildAssessmentSummary(result));
        dto.setComponents(buildComponents(result, breakdown));
        dto.setRiskSignals(buildRiskSignals(result, breakdown));
        dto.setContextHints(buildContextHints(result, breakdown));
        dto.setConstraints(defaultConstraints());

        return dto;
    }

    private RequestMetadataDTO buildMetadata(AssessmentResult result) {

        return new RequestMetadataDTO(
                UUID.randomUUID().toString(),
                result.getQuestionnaireId(),
                result.getQuestionnaireId().toUpperCase(),
                "en",
                Instant.now()
        );
    }

    private AssessmentSummaryDTO buildAssessmentSummary(
            AssessmentResult result
    ) {
        return new AssessmentSummaryDTO(
                result.getFinalScore(),
                result.getSeverityLabel(),
                result.getDescription(),
                null,
                null
        );
    }

    private List<ComponentInsightDTO> buildComponents(
            AssessmentResult result,
            AssessmentBreakdown breakdown
    ) {

        Map<String, Double> componentScores =
                result.getComponentScores();

        if (componentScores == null) return List.of();

        return breakdown.getComponents()
                .stream()
                .filter(c -> componentScores.containsKey(c.getComponentId()))
                .map(c -> new ComponentInsightDTO(
                        c.getComponentId(),
                        c.getComponentName(),
                        componentScores.get(c.getComponentId()),
                        null,
                        extractQuestionIds(c)
                ))
                .toList();
    }

    private List<String> extractQuestionIds(ComponentBreakdown component) {
        return component.getInputs()
                .stream()
                .map(ComponentInputBreakdown::getQuestionId)
                .distinct()
                .toList();
    }

    private List<RiskSignalDTO> buildRiskSignals(
            AssessmentResult result,
            AssessmentBreakdown breakdown
    ) {

        List<RiskSignalDTO> signals = new ArrayList<>();

        Map<String, Double> components = result.getComponentScores();
        if (components == null) return signals;

        // PHQ-9 suicide risk example
        if (components.containsKey("suicide_risk_flag")) {
            double value = components.get("suicide_risk_flag");

            signals.add(new RiskSignalDTO(
                    "suicide_risk_flag",
                    value > 0 ? "HIGH" : "NONE",
                    "phq9_q9",
                    value > 0
                            ? "Self-harm thoughts were reported."
                            : "No self-harm thoughts reported."
            ));
        }

        return signals;
    }

    private Map<String, Object> buildContextHints(
            AssessmentResult result,
            AssessmentBreakdown breakdown
    ) {

        Map<String, Object> hints = new HashMap<>();

        hints.put(
                "scoreAboveClinicalCutoff",
                result.getFinalScore() > 5 // example heuristic
        );

        hints.put(
                "componentCount",
                result.getComponentScores() != null
                        ? result.getComponentScores().size()
                        : 0
        );

        return hints;
    }

    private AIResponseConstraintsDTO defaultConstraints() {

        return new AIResponseConstraintsDTO(
                false,  // allowDiagnosis
                false,  // allowTreatmentAdvice
                true,   // requireDisclaimer
                400     // max tokens
        );
    }
}
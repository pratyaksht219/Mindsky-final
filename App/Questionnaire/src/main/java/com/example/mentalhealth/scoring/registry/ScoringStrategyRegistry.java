package com.example.mentalhealth.scoring.registry;
import com.example.mentalhealth.questionnaire.DTO.QuestionDTO;
import com.example.mentalhealth.questionnaire.DTO.QuestionnaireDTO;
import com.example.mentalhealth.questionnaire.DTO.ResponseFormatDTO;
import com.example.mentalhealth.questionnaire.DTO.ResponseType;
import com.example.mentalhealth.questionnaire.registry.QuestionnaireRegistry;
import com.example.mentalhealth.scoring.DTO.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ScoringStrategyRegistry {

    private final Map<String, ScoringDTO> registry = new LinkedHashMap<>();
    private final ObjectMapper objectMapper;
    private final QuestionnaireRegistry questionnaireRegistry;

    public ScoringStrategyRegistry(QuestionnaireRegistry questionnaireRegistry) {
        this.questionnaireRegistry = questionnaireRegistry;
        this.objectMapper = new ObjectMapper()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
    }

    @PostConstruct
    public void loadScoringDefinitions() {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                    .getResources("classpath:scoring_json_files/*.json");

            for (Resource resource : resources) {
                if (resource.contentLength() == 0) continue;

                ScoringDTO dto = objectMapper.readValue(
                        resource.getInputStream(),
                        ScoringDTO.class
                );

                validateScoring(dto);

                registry.put(dto.getId(), dto);
                log.info("Loaded Scoring Logic: {} for Questionnaire: {}",
                        dto.getId(), dto.getQuestionnaireId());
            }
        } catch (Exception e) {
            log.error("Failed to initialize ScoringStrategyRegistry", e);
            throw new RuntimeException(e);
        }
    }

    private void validateScoring(ScoringDTO scoring) {
        QuestionnaireDTO questionnaire = questionnaireRegistry
                .getById(scoring.getQuestionnaireId())
                .orElseThrow(() -> new IllegalStateException(
                        "Scoring " + scoring.getId() +
                                " references unknown questionnaire " +
                                scoring.getQuestionnaireId()
                ));

        Map<String, QuestionDTO> questionMap =
                questionnaire.getQuestions().stream()
                        .collect(Collectors.toMap(QuestionDTO::getId, q -> q));

        // Validate response mappings
        for (ResponseMappingDTO rm : scoring.getResponses()) {
            QuestionDTO question = questionMap.get(rm.getQuestionId());
            if (question == null) {
                throw new IllegalStateException(
                        "Scoring " + scoring.getId() +
                                " references invalid question " + rm.getQuestionId()
                );
            }

            validateResponseKey(question, rm.getResponseKey());
        }

        // Validate component inputs
        for (ComponentDTO component : scoring.getComponents()) {
            for (InputDTO input : component.getCalculation().getInputs()) {
                QuestionDTO question = questionMap.get(input.getQuestionId());
                if (question == null) {
                    throw new IllegalStateException(
                            "Component " + component.getId() +
                                    " references invalid question " + input.getQuestionId()
                    );
                }

                validateResponseKey(question, input.getResponseKey());
            }
        }
    }

    private void validateResponseKey(QuestionDTO question, String responseKey) {
        ResponseFormatDTO rf = question.getResponseFormat();
        ResponseType type = rf.getType();

        if (type == ResponseType.DUAL_SCALE) {

            if (rf.getResponseKeys() == null || rf.getResponseKeys().isEmpty()) {
                throw new IllegalStateException(
                        "dual_scale question " + question.getId() +
                                " must define response_keys"
                );
            }

            if (!rf.getResponseKeys().containsKey(responseKey)) {
                throw new IllegalStateException(
                        "Invalid response_key '" + responseKey +
                                "' for dual_scale question " + question.getId() +
                                ". Valid keys: " + rf.getResponseKeys().keySet()
                );
            }

        } else {

            if (rf.getResponseKey() == null) {
                throw new IllegalStateException(
                        "Question " + question.getId() +
                                " must define response_key for type " + type
                );
            }

            if (!rf.getResponseKey().equals(responseKey)) {
                throw new IllegalStateException(
                        "Invalid response_key '" + responseKey +
                                "' for question " + question.getId() +
                                ". Expected: " + rf.getResponseKey()
                );
            }
        }
    }

    public Optional<ScoringDTO> getById(String scoringId) {
        return Optional.ofNullable(registry.get(scoringId));
    }

    public Optional<ScoringDTO> getByQuestionnaireId(String questionnaireId) {
        return registry.values().stream()
                .filter(s -> s.getQuestionnaireId().equals(questionnaireId))
                .findFirst();
    }

    public Map<String, ScoringDTO> getAll() {
        return Collections.unmodifiableMap(registry);
    }
}
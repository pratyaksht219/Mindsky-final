package com.example.mentalhealth.questionnaire.registry;



import com.example.mentalhealth.questionnaire.DTO.QuestionDTO;
import com.example.mentalhealth.questionnaire.DTO.QuestionnaireDTO;


import com.example.mentalhealth.questionnaire.DTO.ResponseFormatDTO;
import com.example.mentalhealth.questionnaire.DTO.ResponseType;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.util.*;

@Slf4j
@Component
public class QuestionnaireRegistry {

    private final Map<String, QuestionnaireDTO> registry = new LinkedHashMap<>();
    private final ObjectMapper objectMapper;

    public QuestionnaireRegistry() {
        this.objectMapper = new ObjectMapper()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
    }

    @PostConstruct
    public void loadDefinitions() {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                    .getResources("classpath:questionnaire_json_files/*.json");

            for (Resource resource : resources) {
                if (resource.contentLength() == 0) continue;

                QuestionnaireDTO questionnaire = objectMapper.readValue(
                        resource.getInputStream(),
                        QuestionnaireDTO.class
                );

                validateQuestionnaire(questionnaire);

                registry.put(questionnaire.getId(), questionnaire);
                log.info("Loaded Questionnaire: {} ({})",
                        questionnaire.getName(), questionnaire.getId());
            }
        } catch (Exception e) {
            log.error("Failed to initialize QuestionnaireRegistry", e);
            throw new RuntimeException(e);
        }
    }

    // ===================== VALIDATION =====================

    private void validateQuestionnaire(QuestionnaireDTO questionnaire) {

        if (questionnaire.getId() == null || questionnaire.getId().isBlank()) {
            throw new IllegalStateException("Questionnaire ID is missing");
        }

        if (questionnaire.getQuestions() == null || questionnaire.getQuestions().isEmpty()) {
            throw new IllegalStateException(
                    "Questionnaire " + questionnaire.getId() + " has no questions"
            );
        }

        Set<String> questionIds = new HashSet<>();

        for (QuestionDTO question : questionnaire.getQuestions()) {

            if (!questionIds.add(question.getId())) {
                throw new IllegalStateException(
                        "Duplicate question ID '" + question.getId() +
                                "' in questionnaire " + questionnaire.getId()
                );
            }

            validateResponseFormat(questionnaire.getId(), question);
        }
    }

    private void validateResponseFormat(String questionnaireId, QuestionDTO question) {

        ResponseFormatDTO rf = question.getResponseFormat();

        if (rf == null) {
            throw new IllegalStateException(
                    "Question " + question.getId() +
                            " in questionnaire " + questionnaireId +
                            " has no response_format"
            );
        }

        ResponseType type = rf.getType();

        if (type == null) {
            throw new IllegalStateException(
                    "Question " + question.getId() +
                            " has null response_format.type"
            );
        }

        switch (type) {

            case SCALE -> {
                require(question.getId(), rf.getResponseKey(), "response_key");
                require(question.getId(), rf.getScale(), "scale");
            }

            case DUAL_SCALE -> {
                if (rf.getResponseKeys() == null || rf.getResponseKeys().size() != 2) {
                    throw new IllegalStateException(
                            "Dual-scale question " + question.getId() +
                                    " must define exactly 2 response_keys"
                    );
                }
            }

            case NUMBER -> {
                require(question.getId(), rf.getResponseKey(), "response_key");
                if (rf.getMin() == null) {
                    throw new IllegalStateException(
                            "Number question " + question.getId() +
                                    " must define min value"
                    );
                }
            }

            case TIME, BOOLEAN, TEXT -> {
                require(question.getId(), rf.getResponseKey(), "response_key");
            }

            default -> throw new IllegalStateException(
                    "Unsupported response type " + type +
                            " in question " + question.getId()
            );
        }
    }

    private void require(String questionId, Object value, String fieldName) {
        if (value == null) {
            throw new IllegalStateException(
                    "Question " + questionId +
                            " is missing required field: " + fieldName
            );
        }
    }

    // ===================== ACCESS =====================

    public Optional<QuestionnaireDTO> getById(String id) {
        return Optional.ofNullable(registry.get(id));
    }

    public Map<String, QuestionnaireDTO> getAll() {
        return Collections.unmodifiableMap(registry);
    }
}
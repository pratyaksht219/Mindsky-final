package com.example.mentalhealth.screening.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;

@Getter
@Component
public class ScreeningTaxonomy {

    private Map<String, String> domainToQuestionnaire;

    private String fallbackQuestionnaire;

    private Map<String, MicroSignalDefinition> signals;

    private Map<String, String> keywordToSignal;

    private Map<String, java.util.List<String>> emergencyKeywords;

    @PostConstruct
    public void load() {

        try {

            ObjectMapper mapper = new ObjectMapper();

            InputStream inputStream =
                    new ClassPathResource("screening-taxonomy.json")
                            .getInputStream();

            TaxonomyFile file =
                    mapper.readValue(inputStream, TaxonomyFile.class);

            this.domainToQuestionnaire = file.getDomains();
            this.fallbackQuestionnaire = file.getFallbackQuestionnaire();
            this.signals = file.getMicroSignals();
            this.keywordToSignal = file.getKeywords();
            this.emergencyKeywords = file.getEmergencyKeywords();

        } catch (Exception e) {
            throw new RuntimeException("Failed to load screening taxonomy", e);
        }
    }
}
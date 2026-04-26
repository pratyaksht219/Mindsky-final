package com.example.mentalhealth.screening.engine;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TaxonomyFile {

    private Map<String, String> domains;

    private String fallbackQuestionnaire;

    private Map<String, MicroSignalDefinition> microSignals;

    private Map<String, String> keywords;

    private Map<String, List<String>> emergencyKeywords;

}
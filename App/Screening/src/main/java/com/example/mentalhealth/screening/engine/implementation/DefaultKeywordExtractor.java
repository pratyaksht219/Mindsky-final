package com.example.mentalhealth.screening.engine.implementation;

import com.example.mentalhealth.screening.engine.KeywordExtractor;
import com.example.mentalhealth.screening.engine.ScreeningTaxonomy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

//@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultKeywordExtractor implements KeywordExtractor {

    private final ScreeningTaxonomy taxonomy;

    private static final Pattern PUNCTUATION =
            Pattern.compile("[^a-zA-Z0-9\\s]");

    private static final Set<String> STOPWORDS = Set.of(
            "i","am","is","are","the","a","an","and","or","but",
            "to","of","in","on","at","for","with","that","this","it"
    );

    @Override
    public List<String> extract(String message) {

        if (message == null || message.isBlank()) {
            log.debug("Keyword extraction skipped: empty message");
            return List.of();
        }

        String normalized = normalize(message);

        log.debug("Normalized message: {}", normalized);

        String[] tokens = normalized.split("\\s+");

        Set<String> matches = new HashSet<>();

        Map<String, String> keywordMap = taxonomy.getKeywordToSignal();

        for (String token : tokens) {

            if (STOPWORDS.contains(token)) {
                continue;
            }

            if (keywordMap.containsKey(token)) {
                matches.add(token);
            }
        }

        log.debug("Extracted keywords: {}", matches);

        return new ArrayList<>(matches);
    }

    private String normalize(String message) {

        String lower = message.toLowerCase();

        return PUNCTUATION.matcher(lower)
                .replaceAll("")
                .trim();
    }
}
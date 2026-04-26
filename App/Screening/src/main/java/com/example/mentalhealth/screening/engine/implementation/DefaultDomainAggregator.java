package com.example.mentalhealth.screening.engine.implementation;

import com.example.mentalhealth.screening.domain.DomainAggregationResult;
import com.example.mentalhealth.screening.domain.ScreeningSignalState;
import com.example.mentalhealth.screening.engine.DomainAggregator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class DefaultDomainAggregator implements DomainAggregator {

    private static final double CONFLICT_DELTA = 0.10;

    @Override
    public DomainAggregationResult aggregate(ScreeningSignalState state) {

        Map<String, Double> scores =
                state.getDomainSignalScores();

        if (scores.isEmpty()) {

            log.debug("No domain signals detected");

            return new DomainAggregationResult(
                    null,
                    0.0,
                    Map.of(),
                    false
            );
        }

        double total =
                scores.values()
                        .stream()
                        .mapToDouble(Double::doubleValue)
                        .sum();

        Map<String, Double> normalized =
                new HashMap<>();

        scores.forEach((domain, value) ->
                normalized.put(domain, value / total)
        );

        log.debug("Normalized domain scores: {}", normalized);

        List<Map.Entry<String, Double>> sorted =
                new ArrayList<>(normalized.entrySet());

        sorted.sort((a, b) ->
                Double.compare(b.getValue(), a.getValue())
        );

        Map.Entry<String, Double> dominant =
                sorted.get(0);

        boolean conflict = false;

        if (sorted.size() > 1) {

            double second =
                    sorted.get(1).getValue();

            conflict =
                    Math.abs(
                            dominant.getValue() - second
                    ) < CONFLICT_DELTA;
        }

        log.debug(
                "Domain aggregation result | dominant={} | confidence={} | conflict={}",
                dominant.getKey(),
                dominant.getValue(),
                conflict
        );

        return new DomainAggregationResult(
                dominant.getKey(),
                dominant.getValue(),
                normalized,
                conflict
        );
    }
}
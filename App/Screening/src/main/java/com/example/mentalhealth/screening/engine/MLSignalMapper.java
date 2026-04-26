package com.example.mentalhealth.screening.engine;

import com.example.mentalhealth.screening.domain.ScreeningSignalState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class MLSignalMapper {

    public void map(
            Map<String, Double> scores,
            ScreeningSignalState state
    ) {

        Map.Entry<String, Double> top =
                scores.entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue())
                        .orElse(null);

        if (top == null) {
            log.warn("ML classifier returned empty scores");
            return;
        }

        String domain = top.getKey();
        double similarity = top.getValue();

        log.info(
                "Top ML domain detected → {} ({})",
                domain,
                similarity
        );

        if (similarity > 0.45) {

            state.addMLSignal(
                    domain,
                    similarity
            );

        } else {

            log.info(
                    "Top ML similarity below threshold → {} ({})",
                    domain,
                    similarity
            );
        }
    }
}
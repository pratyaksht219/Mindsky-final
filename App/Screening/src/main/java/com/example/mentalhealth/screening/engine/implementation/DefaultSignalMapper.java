package com.example.mentalhealth.screening.engine.implementation;

import com.example.mentalhealth.screening.domain.ScreeningSignalState;
import com.example.mentalhealth.screening.engine.MicroSignalDefinition;
import com.example.mentalhealth.screening.engine.ScreeningTaxonomy;
import com.example.mentalhealth.screening.engine.SignalMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
@Slf4j
//@Component
@RequiredArgsConstructor
public class DefaultSignalMapper implements SignalMapper {

    private final ScreeningTaxonomy taxonomy;

    @Override
    public void map(
            java.util.List<String> keywords,
            ScreeningSignalState state
    ) {

        log.info("SignalMapper received keywords: {}", keywords);

        Map<String, String> keywordToSignal =
                taxonomy.getKeywordToSignal();

        Map<String, MicroSignalDefinition> signals =
                taxonomy.getSignals();

        for (String keyword : keywords) {

            String microSignal = keywordToSignal.get(keyword);

            if (microSignal == null) {
                continue;
            }

            MicroSignalDefinition def = signals.get(microSignal);

            if (def == null) {
                continue;
            }

            String domain = def.getDomain();

            state.addKeyword(keyword);

            state.addMicroSignal(
                    microSignal,
                    domain,
                    def.getWeight()
            );

            log.debug(
                    "Mapped keyword → signal | keyword={} | microSignal={} | domain={} | weight={}",
                    keyword,
                    microSignal,
                    domain,
                    def.getWeight()
            );
        }

        log.info("Domain scores after mapping: {}", state.getDomainSignalScores());
    }
}
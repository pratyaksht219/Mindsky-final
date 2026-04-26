package com.example.mentalhealth.screening.engine.implementation;

import com.example.mentalhealth.screening.domain.EmergencyLevel;
import com.example.mentalhealth.screening.domain.EmergencyMatch;
import com.example.mentalhealth.screening.engine.EmergencyDetector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DefaultEmergencyDetector implements EmergencyDetector {

    private static final List<String> HIGH_RISK_PHRASES = List.of(

            // Direct suicide intent
            "kill myself",
            "want to kill myself",
            "going to kill myself",
            "thinking of killing myself",
            "suicide",
            "commit suicide",
            "planning suicide",
            "end my life",
            "end it all",
            "take my own life",

            // Passive death wishes
            "want to die",
            "wish i was dead",
            "better off dead",
            "i should be dead",
            "i dont want to live",
            "dont want to live anymore",
            "life isnt worth living",
            "no reason to live",
            "nothing to live for",

            // Self harm intent
            "hurt myself",
            "self harm",
            "harm myself",
            "cut myself",
            "cutting myself",
            "burn myself",
            "injure myself on purpose",

            // Ideation / distress signals
            "thinking about dying",
            "thinking about suicide",
            "thinking of ending my life",
            "thoughts of suicide",
            "suicidal thoughts",
            "suicidal ideation",

            // Preparation signals
            "how to kill myself",
            "ways to kill myself",
            "how to commit suicide",
            "planning to end my life",
            "looking for ways to die",

            // Strong hopelessness indicators
            "life is pointless",
            "life is meaningless",
            "i cant go on anymore",
            "i give up on life"
    );

    @Override
    public EmergencyMatch detect(String message) {

        if (message == null) {
            return new EmergencyMatch(EmergencyLevel.NONE, null);
        }

        String normalized =
                message.toLowerCase();

        for (String phrase : HIGH_RISK_PHRASES) {

            if (normalized.contains(phrase)) {

                log.warn(
                        "Emergency phrase detected: {}",
                        phrase
                );

                return new EmergencyMatch(
                        EmergencyLevel.HIGH,
                        phrase
                );
            }
        }

        log.debug("No emergency signals detected");

        return new EmergencyMatch(
                EmergencyLevel.NONE,
                null
        );
    }
}
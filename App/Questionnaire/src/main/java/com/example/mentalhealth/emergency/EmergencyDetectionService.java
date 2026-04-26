package com.example.mentalhealth.emergency;

import com.example.mentalhealth.session.SessionState;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmergencyDetectionService {
    private static final List<String> EMERGENCY_KEYWORDS = List.of(
            "suicide",
            "suicidal",
            "kill myself",
            "don't want to live",
            "end my life",
            "death",
            "self harm"
    );

    public boolean detect(SessionState session, String latestMessage) {

        if (latestMessage == null || latestMessage.isBlank()) {
            return false;
        }

        String text = latestMessage.toLowerCase();

        return EMERGENCY_KEYWORDS.stream()
                .anyMatch(text::contains);
    }
}

package com.example.mentalhealth.util;

import com.example.mentalhealth.aiService.DTO.AIServiceResponseDTO;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class Constants {
    public final String emergencyDisclaimer = "This information is provided for educational and supportive purposes only. It is not a medical diagnosis and cannot replace professional mental health care.";

    public final AIServiceResponseDTO crisisAiResponse = new AIServiceResponseDTO(
            emergencyDisclaimer,
            "It sounds like you may be experiencing significant distress right now. What you’re feeling matters, and reaching out for help is an important and courageous step.",
            "Moments of crisis can feel overwhelming and intense. During times like these, immediate support from trusted people or professionals is especially important.",
            List.of(
                    "Expressions suggesting acute emotional distress",
                    "Indicators that immediate support may be needed"
            ),
            List.of(
                    "Please consider reaching out to local emergency services or a crisis helpline right away",
                    "If possible, contact a trusted friend, family member, or caregiver and let them know how you are feeling",
                    "Stay in a safe environment and avoid being alone if you can"
            ),
            "You are not alone, and help is available. Many people go through moments like this, and support from others can make a real difference."
    );

    public static final String DEFAULT_DISCLAIMER =
            "Disclaimer\n" +
                    "This explanation is generated to help you better understand your " +
                    "assessment results and reflect on how you’ve been feeling. " +
                    "It is not a medical diagnosis and should not be used as a " +
                    "substitute for professional care. " +
                    "If your symptoms feel overwhelming or persistent, " +
                    "seeking support from a qualified mental health professional is strongly encouraged.";

}

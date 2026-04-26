package com.example.mentalhealth.aiService.assessmentInterpretation;

import com.example.mentalhealth.Assessment.AssessmentBreakdown;
import com.example.mentalhealth.Assessment.AssessmentResult;
import com.example.mentalhealth.aiService.AIContextBuilder;
import com.example.mentalhealth.aiService.AIService;
import com.example.mentalhealth.aiService.DTO.AIServiceRequestDTO;
import com.example.mentalhealth.aiService.DTO.AIServiceResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.example.mentalhealth.util.Constants.DEFAULT_DISCLAIMER;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssessmentInterpretationServiceImpl
        implements AssessmentInterpretationService {

    private final AIContextBuilder contextBuilder;
    private final AIService aiServiceClient;

    @Override
    public AIServiceResponseDTO interpret(
            AssessmentResult result,
            AssessmentBreakdown breakdown
    ) {

        String questionnaireId = result.getQuestionnaireId();

        log.info("Starting AI interpretation for questionnaireId={}", questionnaireId);

        try {

            // 1️⃣ Build AI-safe context
            AIServiceRequestDTO request =
                    contextBuilder.build(result, breakdown);

            log.debug("AI context built for questionnaireId={}, finalScore={}",
                    questionnaireId,
                    result.getFinalScore()
            );

            // 2️⃣ Call AI service
            AIServiceResponseDTO response =
                    aiServiceClient.getAiServiceResponse(request);
            if(response.getDisclaimer() == null) {
                response.setDisclaimer(DEFAULT_DISCLAIMER);
            }
            log.info("AI service responded successfully for questionnaireId={}", questionnaireId);

            // 3️⃣ Defensive validation
            validate(response);

            log.debug("AI response validated for questionnaireId={}", questionnaireId);

            return response;

        } catch (Exception ex) {

            log.error(
                    "AI interpretation failed for questionnaireId={}",
                    questionnaireId,
                    ex
            );

            throw ex;
        }
    }

    private void validate(AIServiceResponseDTO response) {

        if (response == null) {
            log.error("AI service returned null response");
            throw new IllegalStateException("AI service returned null response");
        }

        if (response.getSummary() == null || response.getSummary().isBlank()) {
            log.error("AI response missing summary");
            throw new IllegalStateException("AI response missing summary");
        }
    }
}
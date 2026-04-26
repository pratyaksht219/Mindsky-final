package com.example.mentalhealth.scoring.ScoringEngines;

import com.example.mentalhealth.answers.AnswerStore;
import com.example.mentalhealth.Assessment.AssessmentBreakdown;
import com.example.mentalhealth.Assessment.AssessmentBreakdownBuilder;
import com.example.mentalhealth.Assessment.AssessmentResult;
import com.example.mentalhealth.dto.ScoringOutcome;
import com.example.mentalhealth.questionnaire.DTO.QuestionnaireDTO;
import com.example.mentalhealth.questionnaire.registry.QuestionnaireRegistry;
import com.example.mentalhealth.scoring.DTO.ScoringDTO;
import com.example.mentalhealth.scoring.Factory.ScoringServiceFactory;
import com.example.mentalhealth.scoring.ScoringService;
import com.example.mentalhealth.scoring.registry.ScoringStrategyRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultScoringEngine implements ScoringEngine {

    private final QuestionnaireRegistry questionnaireRegistry;
    private final ScoringStrategyRegistry scoringRegistry;
    private final ScoringServiceFactory scoringServiceFactory;
    private final AssessmentBreakdownBuilder breakdownBuilder;

    @Override
    public ScoringOutcome score(
            String questionnaireId,
            AnswerStore answers
    ) {

        // Resolve questionnaire definition
        QuestionnaireDTO questionnaire =
                questionnaireRegistry.getById(questionnaireId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Questionnaire not found: " + questionnaireId
                                )
                        );

        // Resolve scoring definition (JSON)
        ScoringDTO scoringDTO =
                scoringRegistry.getByQuestionnaireId(questionnaireId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "No scoring definition found for questionnaire: "
                                                + questionnaireId
                                )
                        );

        //  Resolve scoring service (Java implementation)
        ScoringService scoringService =
                scoringServiceFactory.getForQuestionnaire(questionnaireId);

        log.info(
                "Scoring questionnaire [{}] using [{}]",
                questionnaireId,
                scoringService.getClass().getSimpleName()
        );

        //  Compute assessment result
        AssessmentResult result =
                scoringService.score(answers, scoringDTO);

        //  Build assessment breakdown
        AssessmentBreakdown breakdown =
                breakdownBuilder.build(
                        questionnaire,
                        scoringDTO,
                        answers,
                        result
                );

        //  Return unified outcome
        return new ScoringOutcome(result, breakdown);
    }
}
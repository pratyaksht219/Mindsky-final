package com.example.mentalhealth.screening.engine.implementation;

import com.example.mentalhealth.screening.domain.ScreeningSignalState;
import com.example.mentalhealth.screening.domain.ScreeningSession;
import com.example.mentalhealth.screening.dto.QuestionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Slf4j
@Component
public class ScreeningQuestionSelector {

    public QuestionDTO firstQuestion() {
        log.debug("Selecting first screening question");

        return new QuestionDTO(
                "screen_q1",
                "What has been bothering you recently?",
                "general"
        );
    }

    public QuestionDTO nextQuestion(ScreeningSession session) {

        log.debug(
                "Selecting next question | turn={}",
                session.getTurnCount()
        );
        ScreeningSignalState state = session.getSignalState();

        int turn = state.getTurnCount();

        Optional<String> dominantDomain =
                state.getDominantDomain()
                        .map(e -> e.getKey());

        if (turn == 1) {
            return clarificationQuestion(dominantDomain);
        }

        if (turn == 2) {
            return severityQuestion(dominantDomain);
        }

        if (turn >= 3) {
            log.debug("Reached max screening turns");
            return finalClarificationQuestion(dominantDomain);
        }

        return fallbackQuestion();
    }

    private QuestionDTO clarificationQuestion(Optional<String> domain) {

        if (domain.isEmpty()) {
            return fallbackQuestion();
        }

        switch (domain.get()) {

            case "ANXIETY":
                return new QuestionDTO(
                        "screen_anxiety_1",
                        "Do you experience sudden panic attacks or constant worrying?",
                        "anxiety"
                );

            case "DEPRESSION":
                return new QuestionDTO(
                        "screen_depression_1",
                        "Have you been feeling persistently sad or losing interest in activities you once enjoyed?",
                        "depression"
                );

            case "SLEEP":
                return new QuestionDTO(
                        "screen_sleep_1",
                        "Are you having trouble falling asleep, staying asleep, or waking up too early?",
                        "sleep"
                );

            case "ADHD":
                return new QuestionDTO(
                        "screen_adhd_1",
                        "Do you often struggle with staying focused, finishing tasks, or organizing your work?",
                        "adhd"
                );

            case "STRESS":
                return new QuestionDTO(
                        "screen_stress_1",
                        "Do you feel overwhelmed or unable to cope with daily responsibilities?",
                        "stress"
                );

            case "SOCIAL_ANXIETY":
                return new QuestionDTO(
                        "screen_social_1",
                        "Do social situations make you feel extremely anxious or worried about being judged?",
                        "social_anxiety"
                );

            case "TRAUMA":
                return new QuestionDTO(
                        "screen_trauma_1",
                        "Have you experienced disturbing memories, nightmares, or flashbacks related to past events?",
                        "trauma"
                );

            default:
                return fallbackQuestion();
        }
    }

    private QuestionDTO severityQuestion(Optional<String> domain) {

        return new QuestionDTO(
                "screen_severity",
                "How much are these problems affecting your daily life, work, or relationships?",
                domain.orElse("general")
        );
    }

    private QuestionDTO finalClarificationQuestion(Optional<String> domain) {

        return new QuestionDTO(
                "screen_duration",
                "How long have you been experiencing these difficulties?",
                domain.orElse("general")
        );
    }

    private QuestionDTO fallbackQuestion() {

        return new QuestionDTO(
                "screen_general",
                "Can you tell me a little more about what you have been experiencing?",
                "general"
        );
    }
}
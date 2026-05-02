package com.example.mentalhealth.screening.engine.implementation;

import com.example.mentalhealth.screening.domain.ScreeningSignalState;
import com.example.mentalhealth.screening.domain.ScreeningSession;
import com.example.mentalhealth.screening.dto.QuestionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Component
public class ScreeningQuestionSelector {

        private final Random random = new Random();

        private static final List<String> FIRST_QUESTIONS = List.of(
                        "Hi there👋!What's been on your mind lately?",
                        "Hello🌟!How have you been feeling recently?",
                        "Hey! I'm here to listen 💙.\nWhat brings you here today?",
                        "Hi buddy🤗!What has been bothering you recently?",
                        "Hi! Is there anything specific you'd like to talk about today? 💭",
                        "Hello there! How are things going with you lately? 🧘",
                        "Hey! What's been taking up your mental space recently? 🌱");

        private static final List<String> SEVERITY_QUESTIONS = List.of(
                        "I understand. 🫂 How much are these feelings affecting your daily life or work?",
                        "That sounds tough. 😔 To what extent is this interfering with your normal routine?",
                        "I hear you. 💭 How severely is this impacting your day-to-day activities?",
                        "Got it. 🤍 Is this making it difficult for you to get through your usual day?",
                        "It takes courage to share this. ❤️ Is this making it hard to focus on your work or studies?",
                        "I'm sorry you're going through this. 🌧️ How much of your day feels consumed by these feelings?",
                        "That sounds exhausting. 🫂 Are these difficulties affecting your relationships with others?");

        private static final List<String> DURATION_QUESTIONS = List.of(
                        "It helps to know the timeline. ⏳ About how long have you been experiencing this?",
                        "When did you first start noticing these feelings? 🗓️",
                        "Roughly how long has this been going on? 🕰️",
                        "Can you remember when these difficulties first began? 🗓️",
                        "Have you been feeling this way for days, weeks, or longer? 📆",
                        "Can you recall when this started affecting you so much? 🕰️",
                        "Have these feelings been building up over a long time? ⏳");

        private static final List<String> FALLBACK_QUESTIONS = List.of(
                        "Could you tell me a little more about what you're experiencing? 🤔",
                        "I'd love to understand better. 💙 Could you elaborate on that?",
                        "I see. 🪴 Can you share a bit more detail about how you feel?",
                        "It's okay to take your time. 🛋️ Could you explain a little more?",
                        "I'm listening. 👂 Would you feel comfortable sharing a bit more detail?",
                        "I want to make sure I understand. 🧩 Can you give me an example of how this feels?",
                        "Whatever you're feeling is valid. 🪴 Is there anything else you'd like to add?");

        private static final Map<String, List<String>> CLARIFICATION_QUESTIONS = Map.of(
                        "ANXIETY", List.of(
                                        "Do you experience sudden panic attacks or constant worrying?",
                                        "Have you been feeling on edge or excessively nervous?",
                                        "Do you find it hard to stop worrying about things?",
                                        "Do you often feel like something terrible is about to happen?",
                                        "Do you experience physical symptoms like a racing heart or sweating when you feel nervous?"),
                        "DEPRESSION", List.of(
                                        "Have you been feeling persistently sad or losing interest in activities you once enjoyed?",
                                        "Do you often feel down, depressed, or hopeless?",
                                        "Have you noticed a lack of energy or feeling flat most days?",
                                        "Have you been feeling a sense of emptiness or hopelessness about the future?",
                                        "Do you find it hard to get out of bed or start your day?"),
                        "SLEEP", List.of(
                                        "Are you having trouble falling asleep, staying asleep, or waking up too early?",
                                        "How has your sleep quality been recently?",
                                        "Do you wake up feeling unrefreshed or struggle with insomnia?",
                                        "Do you often wake up in the middle of the night and find it hard to go back to sleep?",
                                        "Do you feel excessively sleepy or tired during the daytime?"),
                        "ADHD", List.of(
                                        "Do you often struggle with staying focused, finishing tasks, or organizing your work?",
                                        "Have you been finding it difficult to concentrate or feeling easily distracted?",
                                        "Do you feel restless or find it hard to stick to one task?",
                                        "Do you frequently lose things or forget important details?",
                                        "Does your mind feel like it's constantly racing from one thought to another?"),
                        "STRESS", List.of(
                                        "Do you feel overwhelmed or unable to cope with daily responsibilities?",
                                        "Are you feeling under a lot of pressure lately?",
                                        "Do you feel like you have too much on your plate to handle?",
                                        "Do you feel like you are constantly running out of time to do things?",
                                        "Are you experiencing burnout from your daily tasks?"),
                        "SOCIAL_ANXIETY", List.of(
                                        "Do social situations make you feel extremely anxious or worried about being judged?",
                                        "Do you feel uncomfortable or fearful when interacting with others?",
                                        "Are you avoiding social gatherings because they make you nervous?",
                                        "Do you worry excessively about embarrassing yourself in front of others?",
                                        "Do you often feel self-conscious or easily embarrassed in public?"),
                        "TRAUMA", List.of(
                                        "Have you experienced disturbing memories, nightmares, or flashbacks related to past events?",
                                        "Are you having intrusive thoughts about a difficult past experience?",
                                        "Do you feel constantly on guard because of something that happened in the past?",
                                        "Do you try to avoid places or people that remind you of a past difficult experience?",
                                        "Do you sometimes feel emotionally numb when reminded of a past event?"),
                        "DISTRESS_GENERAL", List.of(
                                        "Do you feel emotionally overwhelmed or unstable recently?",
                                        "Do you feel like your mental well-being is struggling overall?",
                                        "Are your emotions feeling out of control or difficult to manage?",
                                        "Do you feel psychologically exhausted or mentally unwell?",
                                        "Are you feeling emotionally drained by life right now?"),
                        "SOCIAL_SUPPORT", List.of(
                                        "Do you feel like you lack a support system to talk to about your feelings?",
                                        "Are you feeling isolated or disconnected from friends and family?",
                                        "Do you feel like no one truly understands what you are going through?",
                                        "Do you feel completely alone with your problems?",
                                        "Are you finding it hard to find someone to share your joys and sorrows with?"));

        private String pickRandom(List<String> questions) {
                return questions.get(random.nextInt(questions.size()));
        }

        public QuestionDTO firstQuestion() {
                log.debug("Selecting first screening question");
                return new QuestionDTO(
                                "screen_q1",
                                pickRandom(FIRST_QUESTIONS),
                                "general");
        }

        public QuestionDTO nextQuestion(ScreeningSession session) {
                log.debug("Selecting next question | turn={}", session.getTurnCount());
                ScreeningSignalState state = session.getSignalState();
                int turn = state.getTurnCount();

                Optional<String> dominantDomain = state.getDominantDomain().map(Map.Entry::getKey);

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
                if (domain.isEmpty() || !CLARIFICATION_QUESTIONS.containsKey(domain.get())) {
                        return fallbackQuestion();
                }

                String d = domain.get();
                return new QuestionDTO(
                                "screen_" + d.toLowerCase() + "_1",
                                pickRandom(CLARIFICATION_QUESTIONS.get(d)),
                                d.toLowerCase());
        }

        private QuestionDTO severityQuestion(Optional<String> domain) {
                return new QuestionDTO(
                                "screen_severity",
                                pickRandom(SEVERITY_QUESTIONS),
                                domain.orElse("general").toLowerCase());
        }

        private QuestionDTO finalClarificationQuestion(Optional<String> domain) {
                return new QuestionDTO(
                                "screen_duration",
                                pickRandom(DURATION_QUESTIONS),
                                domain.orElse("general").toLowerCase());
        }

        private QuestionDTO fallbackQuestion() {
                return new QuestionDTO(
                                "screen_general",
                                pickRandom(FALLBACK_QUESTIONS),
                                "general");
        }
}
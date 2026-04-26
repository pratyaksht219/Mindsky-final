package com.example.mentalhealth.answers;

import com.example.mentalhealth.questionnaire.DTO.QuestionDTO;

import java.util.Map;

public class AnswerExtractor {

    public static Double extractScaleValue(
            AnswerSubmissionDTO submission,
            QuestionDTO question
    ) {

        // 1️⃣ simple numeric answer
        if (submission.getAnswer() != null) {
            return submission.getAnswer().doubleValue();
        }

        // 2️⃣ responses map
        Map<String, Object> responses = submission.getResponses();

        if (responses == null) {
            throw new IllegalArgumentException(
                    "Answer or responses must be provided"
            );
        }

        String key = question.getResponseFormat().getResponseKey();

        Object value = responses.get(key);

        if (value == null) {
            throw new IllegalArgumentException(
                    "Missing response for key: " + key
            );
        }

        if (!(value instanceof Number)) {
            throw new IllegalArgumentException(
                    "Response must be numeric for SCALE questions"
            );
        }

        return ((Number) value).doubleValue();
    }
}
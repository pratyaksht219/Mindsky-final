package com.example.mentalhealth.answers;

import java.util.Map;
import java.util.Optional;

public abstract class AnswerStore {

    /** Store a value for a question + response_key */
    public abstract void put(
            String questionId,
            String responseKey,
            Object value
    );

    /** Retrieve a raw value */
    public abstract Optional<Object> get(
            String questionId,
            String responseKey
    );



    /** Check existence */
    public abstract boolean contains(
            String questionId,
            String responseKey
    );

    /** Retrieve all answers (read-only) */
    public abstract Map<String, Map<String, Object>> getAll();

    /** Retrieve all answers for a single question */
    public abstract Map<String, Object> getAllForQuestion(String questionId);

    /** Clear all stored answers */
    public abstract void clear();

    /* ---------- Typed helpers ---------- */

    public Optional<Integer> getInt(String questionId, String responseKey) {
        return get(questionId, responseKey)
                .filter(v -> v instanceof Number)
                .map(v -> ((Number) v).intValue());
    }

    public Optional<Double> getDouble(String questionId, String responseKey) {
        return get(questionId, responseKey)
                .filter(v -> v instanceof Number)
                .map(v -> ((Number) v).doubleValue());
    }

    public Optional<Boolean> getBoolean(String questionId, String responseKey) {
        return get(questionId, responseKey)
                .filter(v -> v instanceof Boolean)
                .map(v -> (Boolean) v);
    }

    public Optional<String> getString(String questionId, String responseKey) {
        return get(questionId, responseKey)
                .filter(v -> v instanceof String)
                .map(v -> (String) v);
    }
}
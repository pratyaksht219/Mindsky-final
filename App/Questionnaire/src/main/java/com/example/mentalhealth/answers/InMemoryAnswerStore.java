package com.example.mentalhealth.answers;

import java.util.*;


public class InMemoryAnswerStore extends AnswerStore {

    private final Map<String, Map<String, Object>> store = new HashMap<>();

    @Override
    public void put(String questionId, String responseKey, Object value) {
        store
                .computeIfAbsent(questionId, k -> new HashMap<>())
                .put(responseKey, value);
    }

    @Override
    public Optional<Object> get(String questionId, String responseKey) {
        return Optional.ofNullable(
                store.getOrDefault(questionId, Map.of())
                        .get(responseKey)
        );
    }



    @Override
    public boolean contains(String questionId, String responseKey) {
        return store.containsKey(questionId)
                && store.get(questionId).containsKey(responseKey);
    }

    @Override
    public Map<String, Map<String, Object>> getAll() {
        return Collections.unmodifiableMap(store);
    }

    @Override
    public Map<String, Object> getAllForQuestion(String questionId) {
        return store.getOrDefault(questionId, Map.of());
    }

    @Override
    public void clear() {
        store.clear();
    }
}
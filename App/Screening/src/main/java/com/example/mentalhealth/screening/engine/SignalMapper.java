package com.example.mentalhealth.screening.engine;

import com.example.mentalhealth.screening.domain.ScreeningSignalState;

import java.util.List;

public interface SignalMapper {

    void map(
            List<String> keywords,
            ScreeningSignalState state
    );

}
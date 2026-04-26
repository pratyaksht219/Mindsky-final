package com.example.mentalhealth.screening.engine;

import com.example.mentalhealth.screening.domain.*;

public interface KeywordScreeningEngine {

    ScreeningDecision evaluate(
            ScreeningSignalState state,
            EmergencyMatch emergency
    );

}
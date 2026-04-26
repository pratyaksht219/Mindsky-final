package com.example.mentalhealth.screening.engine;

import com.example.mentalhealth.screening.domain.DomainAggregationResult;
import com.example.mentalhealth.screening.domain.ScreeningSignalState;

public interface DomainAggregator {

    DomainAggregationResult aggregate(ScreeningSignalState state);

}
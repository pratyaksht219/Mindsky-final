package com.example.mentalhealth.screening.engine;

import com.example.mentalhealth.screening.domain.EmergencyMatch;

public interface EmergencyDetector {

    EmergencyMatch detect(String message);

}
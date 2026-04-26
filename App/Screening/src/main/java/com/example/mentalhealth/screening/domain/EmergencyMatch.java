package com.example.mentalhealth.screening.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmergencyMatch {

    private EmergencyLevel level;

    private String matchedPhrase;

}
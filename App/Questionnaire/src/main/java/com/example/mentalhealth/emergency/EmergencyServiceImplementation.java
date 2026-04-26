package com.example.mentalhealth.emergency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class EmergencyServiceImplementation implements EmergencyService {
    private static final String emergencyServiceURL = "PASTE YOUR EMERGENCY SERVICE URL HERE";

    @Autowired
    private WebClient.Builder builder;

    private WebClient getEmergencyClient(){
        return builder.build();
    }

    @Override
    public String getEmergencyResponse(){
        return getEmergencyClient()
                .get()
                .uri(emergencyServiceURL)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }




}

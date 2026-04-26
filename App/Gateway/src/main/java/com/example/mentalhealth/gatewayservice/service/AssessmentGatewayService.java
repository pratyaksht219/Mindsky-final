package com.example.mentalhealth.gatewayservice.service;

import com.example.mentalhealth.gatewayservice.dto.gateway.GatewayAnswerRequest;
import com.example.mentalhealth.gatewayservice.dto.gateway.GatewayStartRequest;
import org.springframework.http.ResponseEntity;

public interface AssessmentGatewayService {

    ResponseEntity<Object> startFlow(GatewayStartRequest request);

    ResponseEntity<Object> continueFlow(GatewayAnswerRequest request);

}
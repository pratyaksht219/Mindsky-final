package com.example.mentalhealth.gatewayservice.controller;

import com.example.mentalhealth.gatewayservice.dto.gateway.GatewayAnswerRequest;
import com.example.mentalhealth.gatewayservice.dto.gateway.GatewayStartRequest;
import com.example.mentalhealth.gatewayservice.service.AssessmentGatewayService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gateway")
@RequiredArgsConstructor
public class AssessmentGatewayController {

    private final AssessmentGatewayService gatewayService;

    @PostMapping("/start")
    public ResponseEntity<Object> start(
            @RequestBody GatewayStartRequest request
    ) {
        return gatewayService.startFlow(request);
    }

    @PostMapping("/answer")
    public ResponseEntity<Object> answer(
            @RequestBody GatewayAnswerRequest request
    ) {
        return gatewayService.continueFlow(request);
    }
}
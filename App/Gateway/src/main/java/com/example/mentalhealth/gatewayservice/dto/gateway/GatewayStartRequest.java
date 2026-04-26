package com.example.mentalhealth.gatewayservice.dto.gateway;

import lombok.Data;

@Data
public class GatewayStartRequest {

    private String message;

    private String correlationId;

}
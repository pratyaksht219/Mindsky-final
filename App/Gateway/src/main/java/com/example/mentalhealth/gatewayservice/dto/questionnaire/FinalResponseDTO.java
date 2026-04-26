package com.example.mentalhealth.gatewayservice.dto.questionnaire;

import com.example.mentalhealth.gatewayservice.dto.screening.QuestionDTO;
import lombok.Data;

@Data
public class FinalResponseDTO {

    private String sessionId;

    private String phase;

    private QuestionDTO nextQuestion;

    private Object result;

    private Object breakdown;

    private Object aiServiceResponse;


}
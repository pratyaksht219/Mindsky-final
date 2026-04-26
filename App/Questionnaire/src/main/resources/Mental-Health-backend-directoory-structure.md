mental-health-backend/
│
├── pom.xml
├── Dockerfile
├── README.md
│
└── src/
    └── main/
        ├── java/com/app/mentalhealth/
        │   ├── MentalHealthApplication.java
        │   │
        │   ├── config/
        │   │   ├── SecurityConfig.java
        │   │   ├── RedisConfig.java
        │   │   ├── WebClientConfig.java
        │   │   └── JpaConfig.java
        │   │
        │   ├── controller/
        │   │   └── ChatController.java
        │   │
        │   ├── screening/
        │   │   ├── SemanticScreeningService.java
        │   │   └── ScreeningResult.java
        │   │
        │   ├── selection/
        │   │   └── QuestionnaireSelectionEngine.java
        │   │
        │   ├── questionnaire/
        │   │   ├── QuestionnaireService.java        # Common interface
        │   │   ├── impl/
        │   │   │   ├── Gad7QuestionnaireService.java
        │   │   │   ├── Phq9QuestionnaireService.java
        │   │   │   ├── Dass21QuestionnaireService.java
        │   │   │   ├── Pcl5QuestionnaireService.java
        │   │   │   └── AsrsQuestionnaireService.java
        │   │   │
        │   │   ├── model/
        │   │   │   ├── Questionnaire.java
        │   │   │   ├── Question.java
        │   │   │   ├── ResponseOption.java
        │   │   │   └── SeverityLevel.java
        │   │   │
        │   │   └── registry/
        │   │       └── QuestionnaireRegistry.java
        │   │
        │   ├── session/
        │   │   ├── SessionState.java
        │   │   ├── QuestionnaireSessionState.java
        │   │   ├── ScreeningSessionState.java
        │   │   ├── SessionService.java
        │   │   └── SessionRepository.java
        │   │
        │   ├── persistence/
        │   │   ├── entity/
        │   │   │   ├── AssessmentResultEntity.java
        │   │   │   └── AnswerEntity.java
        │   │   │
        │   │   ├── repository/
        │   │   │   ├── AssessmentResultRepository.java
        │   │   │   └── AnswerRepository.java
        │   │   │
        │   │   └── service/
        │   │       └── AssessmentPersistenceService.java
        │   │
        │   ├── ai/
        │   │   ├── AiServiceClient.java
        │   │   ├── dto/
        │   │   │   ├── AiRequest.java
        │   │   │   └── AiResponse.java
        │   │   └── prompt/
        │   │       └── PromptBuilder.java
        │   │
        │   ├── security/
        │   │   ├── JwtFilter.java
        │   │   ├── JwtUtil.java
        │   │   └── AuthEntryPoint.java
        │   │
        │   ├── emergency/
        │   │   ├── CrisisDetectionService.java
        │   │   └── EmergencyRoutingService.java
        │   │
        │   ├── dto/
        │   │   ├── ChatRequest.java
        │   │   └── ChatResponse.java
        │   │
        │   ├── util/
        │   │   ├── Constants.java
        │   │   └── ScoreUtils.java
        │   │
        │   └── exception/
        │       ├── GlobalExceptionHandler.java
        │       └── BusinessException.java
        │
        └── resources/
            ├── application.properties
            │
            └── questionnaires/
                ├── gad7.json
                ├── phq9.json
                ├── dass21.json
                ├── pcl5.json
                ├── asrs.json
                └── index.json
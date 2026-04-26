package com.example.mentalhealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponseBreakdown {

    private String questionId;
    private String questionText;

    /** One or more responses per question */
    private List<ResponseValueBreakdown> responses;
}
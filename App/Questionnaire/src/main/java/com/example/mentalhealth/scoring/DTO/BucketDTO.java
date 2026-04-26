package com.example.mentalhealth.scoring.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BucketDTO {
    private Double min;
    private Double max;
    private Double score;
}

package com.example.mentalhealth.scoring.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransformDTO {
    private String type; // identity, reverse, bucket, formula
    private String formula;
    private List<BucketDTO> buckets;
}
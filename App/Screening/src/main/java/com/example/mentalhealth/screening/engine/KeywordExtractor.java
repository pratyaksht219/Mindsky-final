package com.example.mentalhealth.screening.engine;

import java.util.List;



public interface KeywordExtractor {

    List<String> extract(String message);

}
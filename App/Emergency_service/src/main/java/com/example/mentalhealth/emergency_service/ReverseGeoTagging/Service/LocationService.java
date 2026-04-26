package com.example.mentalhealth.emergency_service.ReverseGeoTagging.Service;

import com.example.mentalhealth.emergency_service.ReverseGeoTagging.Repository.StateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final StateRepository stateRepository;

    public String resolveState(double lat, double lon) {
        return stateRepository.findStateByLocation(lat, lon);
    }

    public List<Object[]> resolveDistrict(double lat, double lon) {
        return stateRepository.findDistrictAndState(lat, lon);
    }
}
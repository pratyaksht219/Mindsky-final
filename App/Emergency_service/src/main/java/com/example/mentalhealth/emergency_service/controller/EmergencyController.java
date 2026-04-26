package com.example.mentalhealth.emergency_service.controller;

import com.example.mentalhealth.emergency_service.dto.LocationRequestDTO;
import com.example.mentalhealth.emergency_service.service.CallingService;
import com.example.mentalhealth.emergency_service.ReverseGeoTagging.Service.LocationService;
import com.example.mentalhealth.emergency_service.service.SMSService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/crisis")
@RequiredArgsConstructor
public class EmergencyController {

    private final CallingService callService;
    private final SMSService smsService;
    private final LocationService locationService;


    @PostMapping("/call")
    public ResponseEntity<String> makeCall(@RequestParam String to,
                                           @RequestParam String username) {
        return new ResponseEntity<>(
                callService.makeCall(to,
                        username),
                HttpStatus.OK
        );
    }

    @PostMapping("/sendSms")
    public ResponseEntity<String> sendSms(@RequestBody LocationRequestDTO location,
                                          @RequestParam String to,
                                          @RequestParam String username) {
        String messageId = smsService.sendSMS(
                to,
                location.getLatitude(),
                location.getLongitude(),
                username

        );

        return new ResponseEntity<>(
                 messageId,
                 HttpStatus.OK
        );
    }

    @GetMapping("/locate/district")
    public ResponseEntity<List<Object[]>> getState(
            @RequestParam double lat,
            @RequestParam double lon) {

        return new ResponseEntity<>(
                locationService.resolveDistrict(lat, lon),
                HttpStatus.OK
        );
    }

}

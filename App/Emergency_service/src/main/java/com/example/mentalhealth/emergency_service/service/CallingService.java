package com.example.mentalhealth.emergency_service.service;

import com.twilio.rest.api.v2010.account.Call;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class CallingService {

    @Value("${twilio.phone.number}")
    private String fromNumber;

    public String makeCall(String to, String username) {



        Call call = Call.creator(
                new PhoneNumber(to),
                new PhoneNumber(fromNumber),
                URI.create("http://demo.twilio.com/docs/voice.xml") // Twilio demo voice
        ).create();

        return "CALL MADE to number: " + to
                + "\nCALL SID: " + call.getSid();

//        return "CALL MADE to number: " + to
//                + "\nCALL SID: " + "testCallId123456789";
    }
}

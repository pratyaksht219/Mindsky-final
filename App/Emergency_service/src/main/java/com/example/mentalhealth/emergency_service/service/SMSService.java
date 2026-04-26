package com.example.mentalhealth.emergency_service.service;

import com.example.mentalhealth.emergency_service.Helpline.HelplineData;
import com.example.mentalhealth.emergency_service.ReverseGeoTagging.Service.LocationService;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SMSService {

    @Value("${twilio.phone.number}")
    private String fromNumber;
    private final LocationService locationService;

    public String sendSMS(String to, double latitude, double longitude, String username) {
        // 1. Generate Google Maps link
        String mapsLink = "https://www.google.com/maps?q=" + latitude + "," + longitude;
        String state = locationService.resolveState(latitude, longitude);
        List<Object[]> districts = locationService.resolveDistrict(latitude, longitude);

        String districtName = "Unknown";
        String stateName = state; // fallback

        if (districts != null && !districts.isEmpty()) {
            Object[] row = districts.get(0);

            districtName = row[0] != null ? row[0].toString() : "Unknown";
            stateName = row[1] != null ? row[1].toString() : state;
        }

        Map<String, String> helplines = HelplineData.getStateHelplines();
        String stateHelpline = helplines.getOrDefault(stateName, "1800-599-0019");

        String AasraHelpline = helplines.get("AASRA");
        String NationalEmergencyHelpline = helplines.get("National_emergency");
        String NationalMentalHealthHelpline = helplines.get("Kiran_Mental_Health_Helpline");
        // 2. Create message
        String messageBody = "🚨 Mindsky\n"
                + username +" needs help.\n"
                + "THIS IS A CRISIS ALERT. THE USER IS IN CRITICAL CONDITION. "
                + "Please call following helplines immediately:\n"
                + "State Helpline: " + stateHelpline + "\n"
                + "24x7 Suicide prevention AASRA: " + AasraHelpline + "\n"
                + "National Mental Health helpline " + NationalMentalHealthHelpline + "\n"
                + "National Emergency Helpline: " + NationalEmergencyHelpline + "\n"
                + "or any relative, friend or family member of "+username+"\n"
                + "Location Details:\n"
                + "Latitude: " + latitude + "\n"
                + "Longitude: " + longitude + "\n"
                + "District: " + districtName + "\n"
                + "State: " + stateName + "\n"
                + "Get the accurate Last known location for the user here: "
                + "Location: " + mapsLink;

        System.out.println(messageBody);

//        // 3. Send SMS via Twilio
//        Message message = Message.creator(
//                new PhoneNumber(to),
//                new PhoneNumber(fromNumber),
//                messageBody
//        ).create();

//         send a whatsapp message
        Message message = Message.creator(
                new PhoneNumber("whatsapp:"+to),
                new PhoneNumber("whatsapp:+14155238886"),
                messageBody
        ).create();

        // 4. Return message SID (useful for tracking/logging)
        return "\n SMS SENT to number: " + to + "\n" +
                "MESSAGE SID: " + message.getSid() + "\n\n\n" +
                "MESSAGE BODY: " + messageBody + "\n";

//        return "\n Whatsapp message sent to number: " + to + "\n" +
//                "MESSAGE SID: " + "1234567890" + "\n\n\n" +
//                "MESSAGE BODY: \n" + messageBody + "\n";
    }
}
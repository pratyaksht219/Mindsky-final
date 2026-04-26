package com.example.mentalhealth.emergency_service.Helpline;

import java.util.HashMap;
import java.util.Map;

public class HelplineData {

    public static Map<String, String> getStateHelplines() {
        Map<String, String> helplines = new HashMap<>();
        helplines.put("Kiran_Mental_Health_Helpline", "1800-599-0019");
        helplines.put("Unknown", "1800-599-0019, ");
        helplines.put("AASRA", "+91-9820466726");
        helplines.put("National_emergency", "112");
        // North India
        helplines.put("Delhi", "9152987821");
        helplines.put("Uttar Pradesh", "18001805145");
        helplines.put("Haryana", "08046110007");
        helplines.put("Punjab", "104");
        helplines.put("Himachal Pradesh", "104");
        helplines.put("Uttarakhand", "104");
        helplines.put("Jammu and Kashmir", "01942313149");
        // West India
        helplines.put("Maharashtra", "9152987821");
        helplines.put("Gujarat", "104");
        helplines.put("Rajasthan", "104");
        helplines.put("Goa", "08322252525");
        // South India
        helplines.put("Karnataka", "08046110007");
        helplines.put("Tamil Nadu", "04424640050");
        helplines.put("Kerala", "1056");
        helplines.put("Telangana", "04021111111");
        helplines.put("Andhra Pradesh", "08662410978");
        // East India
        helplines.put("West Bengal", "03324760000");
        helplines.put("Odisha", "104");
        helplines.put("Bihar", "104");
        helplines.put("Jharkhand", "104");
        // Central India
        helplines.put("Madhya Pradesh", "104");
        helplines.put("Chhattisgarh", "104");
        // North-East India
        helplines.put("Assam", "104");
        helplines.put("Meghalaya", "14410");
        helplines.put("Manipur", "104");
        helplines.put("Mizoram", "104");
        helplines.put("Nagaland", "104");
        helplines.put("Tripura", "104");
        helplines.put("Arunachal Pradesh", "104");
        return helplines;
    }
}
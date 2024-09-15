package com.goonok.equalbangla.service;

import com.goonok.equalbangla.geo_locaton.GeoLocationService;
import com.goonok.equalbangla.geo_locaton.TimeZoneService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class GreetingService {

    @Autowired
    private GeoLocationService geoLocationService;

    @Autowired
    private TimeZoneService timeZoneService;

    public String greetUser(HttpServletRequest request) {

        String clientIP = geoLocationService.getClientIP(request);

        // Get the user's time zone based on their IP address
        String timeZone = geoLocationService.getTimeZone(clientIP);
        if (timeZone != null) {
            // Get the local time for the user's time zone
            LocalTime userLocalTime = timeZoneService.getLocalTimeFromTimeZone(timeZone);
            return greet(userLocalTime);

        } else {
            return "Welcome";
        }
    }


    private String greet(LocalTime time) {
        if (time.isAfter(LocalTime.of(4, 59)) && time.isBefore(LocalTime.of(12, 0))) {
            return "Good Morning";
        } else if (time.isAfter(LocalTime.of(11, 59)) && time.isBefore(LocalTime.of(17, 0))) {
            return "Good Afternoon";
        } else if (time.isAfter(LocalTime.of(16, 59))) {
            return "Good Evening";
        } else if (time.isBefore(LocalTime.of(5, 0))) {
            return "Good Evening";
        } else {
            return "Welcome";
        }
    }
}
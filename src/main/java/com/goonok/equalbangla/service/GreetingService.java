package com.goonok.equalbangla.service;

import com.goonok.equalbangla.geo_locaton.GeoLocationService;
import com.goonok.equalbangla.geo_locaton.TimeZoneService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Slf4j
@Service
public class GreetingService {

    @Autowired
    private GeoLocationService geoLocationService;

    @Autowired
    private TimeZoneService timeZoneService;

    private static final String DEFAULT_TIME_ZONE = "Asia/Dhaka";  // Default to Dhaka time zone

    public String greetUser(HttpServletRequest request) {

        String clientIP = geoLocationService.getClientIP(request);

        log.info("clientIP: " + clientIP);
        // Get the user's time zone based on their IP address
        String timeZone = geoLocationService.getTimeZone(clientIP);
        log.info("timeZone: " + timeZone);

        if (timeZone == null) {
            timeZone = DEFAULT_TIME_ZONE;
        }

        // Get the local time for the user's time zone
        LocalTime userLocalTime = timeZoneService.getLocalTimeFromTimeZone(timeZone);
        log.info("userLocalTime: " + userLocalTime);
        return greet(userLocalTime);
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
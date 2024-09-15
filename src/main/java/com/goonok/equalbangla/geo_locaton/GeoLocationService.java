package com.goonok.equalbangla.geo_locaton;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeoLocationService {

    private static final String GEO_API_URL = "http://ip-api.com/json/";

    public String getTimeZone(String ip) {
        RestTemplate restTemplate = new RestTemplate();
        // Make an HTTP GET request and map the response to GeoLocationResponse
        GeoLocationResponse response = restTemplate.getForObject(GEO_API_URL + ip, GeoLocationResponse.class);

        // Return the timezone from the response
        return response != null ? response.getTimezone() : null;
    }


    // Helper method to get client IP address from HttpServletRequest
    public String getClientIP(HttpServletRequest request) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || remoteAddr.isEmpty()) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }
}

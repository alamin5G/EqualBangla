package com.goonok.equalbangla.geo_locaton;

import org.springframework.stereotype.Service;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class TimeZoneService {

    public LocalTime getLocalTimeFromTimeZone(String timeZone) {
        ZoneId zoneId = ZoneId.of(timeZone);
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        return zonedDateTime.toLocalTime();
    }
}

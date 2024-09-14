package com.goonok.equalbangla.service;

import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class GreetingService {

    public String greet(LocalTime time) {

        if (time.isAfter(LocalTime.of(4, 59)) && time.isBefore(LocalTime.of(12, 0))) {
            return "Good Morning";
        } else if (time.isAfter(LocalTime.of(11, 59)) && time.isBefore(LocalTime.of(17, 0))) {
            return "Good Afternoon";
        } else if (time.isAfter(LocalTime.of(16, 59))) {
            return "Good Evening";
        }else if(time.isBefore(LocalTime.of(5, 0))){
            return "Good Evening";
        }else {
            return "Welcome";
        }
    }
}

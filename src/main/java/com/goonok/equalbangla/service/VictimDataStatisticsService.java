package com.goonok.equalbangla.service;

import com.goonok.equalbangla.repository.VictimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VictimDataStatisticsService {

    @Autowired
    private VictimRepository victimRepository;
    // Get statistics for each incident type (e.g., Injured, Missing, Death)
    public Map<String, Long> getIncidentTypeStatistics() {
        List<Object[]> results = victimRepository.countByIncidentType();
        // Convert the Object[] to a Map of incidentType -> count
        Map<String, Long> incidentTypeStats = new HashMap<>();
        for (Object[] result : results) {
            String incidentType = (String) result[0];
            Long count = (Long) result[1];
            incidentTypeStats.put(incidentType, count);
        }
        return incidentTypeStats;
    }

    // Get labels (months) for cases over time chart
    public List<String> getCasesOverTimeLabels() {
        List<Object[]> results = victimRepository.countCasesOverTime();
        List<String> labels = new ArrayList<>();
        for (Object[] result : results) {
            Integer month = (Integer) result[0];
            String monthName = Month.of(month).name(); // Convert month number to name
            labels.add(monthName);
        }
        return labels;
    }

    // Get data (counts) for cases over time chart
    public List<Long> getCasesOverTimeData() {
        List<Object[]> results = victimRepository.countCasesOverTime();
        List<Long> data = new ArrayList<>();
        for (Object[] result : results) {
            Long count = (Long) result[1];
            data.add(count);
        }
        return data;
    }

    // Get labels (distinct locations) for cases by location chart
    public List<String> getCasesByLocationLabels() {
        return victimRepository.getDistinctLocations();
    }

    // Get data (counts) for cases by location chart
    public List<Long> getCasesByLocationData() {
        List<Object[]> results = victimRepository.countCasesByLocation();
        List<Long> data = new ArrayList<>();
        for (Object[] result : results) {
            Long count = (Long) result[1];
            data.add(count);
        }
        return data;
    }

    // Get statistics for verification status (verified and unverified cases)
    public List<Long> getVerificationStatusStatistics() {
        Long verifiedCount = victimRepository.countVerified();
        Long unverifiedCount = victimRepository.countUnverified();
        Long rejectedCount = victimRepository.countRejectedByVerificationStatus();
        return List.of(verifiedCount, unverifiedCount, rejectedCount);
    }
}

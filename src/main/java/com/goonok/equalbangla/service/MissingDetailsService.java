package com.goonok.equalbangla.service;


import com.goonok.equalbangla.model.MissingDetails;
import com.goonok.equalbangla.repository.MissingDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MissingDetailsService {

    @Autowired
    private MissingDetailsRepository missingDetailsRepository;

    // Save missing details
    public MissingDetails saveMissingDetails(MissingDetails missingDetails) {
        return missingDetailsRepository.save(missingDetails);
    }
}
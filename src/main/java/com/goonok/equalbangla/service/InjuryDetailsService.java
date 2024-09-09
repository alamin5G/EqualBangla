package com.goonok.equalbangla.service;


import com.goonok.equalbangla.model.InjuryDetails;
import com.goonok.equalbangla.repository.InjuryDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InjuryDetailsService {

    @Autowired
    private InjuryDetailsRepository injuryDetailsRepository;

    // Save injury details
    public InjuryDetails saveInjuryDetails(InjuryDetails injuryDetails) {
        return injuryDetailsRepository.save(injuryDetails);
    }

}
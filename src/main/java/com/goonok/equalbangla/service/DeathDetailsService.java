package com.goonok.equalbangla.service;


import com.goonok.equalbangla.model.DeathDetails;
import com.goonok.equalbangla.repository.DeathDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeathDetailsService {

    @Autowired
    private DeathDetailsRepository deathDetailsRepository;

    // Save death details
    public DeathDetails saveDeathDetails(DeathDetails deathDetails) {
        return deathDetailsRepository.save(deathDetails);
    }
}
package com.goonok.equalbangla.service;

import com.goonok.equalbangla.model.*;
import com.goonok.equalbangla.repository.DeathDetailsRepository;
import com.goonok.equalbangla.repository.VictimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VictimService {

    @Autowired
    private VictimRepository victimRepository;

    @Autowired
    private InjuryDetailsService injuryDetailsService;

    // Save Death Case
    public void saveDeathCase(Victim victim, DeathDetails deathDetails) {
        victim.setDeathDetails(deathDetails);
        victimRepository.save(victim);
    }

    // Save Missing Case
    public void saveMissingCase(Victim victim, MissingDetails missingDetails) {
        victim.setMissingDetails(missingDetails);
        victimRepository.save(victim);
    }

    public void saveInjuredCase(Victim victim, InjuryDetails injuryDetails) {
        // You don't need to separately save injuryDetails or contactPerson if cascading is set up

        injuryDetailsService.saveInjuryDetails(injuryDetails);
        victim.setInjuryDetails(injuryDetails);
        // Persist the victim, which will automatically persist the associated entities
        victimRepository.save(victim);
    }

    // Other service methods...
}

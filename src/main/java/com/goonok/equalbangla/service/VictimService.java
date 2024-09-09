package com.goonok.equalbangla.service;

import com.goonok.equalbangla.model.Victim;
import com.goonok.equalbangla.model.DeathDetails;
import com.goonok.equalbangla.model.MissingDetails;
import com.goonok.equalbangla.model.InjuryDetails;
import com.goonok.equalbangla.repository.VictimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VictimService {

    @Autowired
    private VictimRepository victimRepository;

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

    // Save Injured Case
    public void saveInjuredCase(Victim victim, InjuryDetails injuryDetails) {
        victim.setInjuryDetails(injuryDetails);
        victimRepository.save(victim);
    }

    // Other service methods...
}

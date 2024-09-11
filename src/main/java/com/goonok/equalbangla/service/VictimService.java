package com.goonok.equalbangla.service;

import com.goonok.equalbangla.model.*;
import com.goonok.equalbangla.repository.DeathDetailsRepository;
import com.goonok.equalbangla.repository.VictimRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
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

        if (missingDetails != null){
            victim.setMissingDetails(missingDetails);
            victimRepository.save(victim);
        }else{
            log.info("missing details is null on the victim service");
        }
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

package com.goonok.equalbangla.service;

import com.goonok.equalbangla.model.*;
import com.goonok.equalbangla.repository.DeathDetailsRepository;
import com.goonok.equalbangla.repository.VictimRepository;
import com.goonok.equalbangla.util.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Service
public class VictimService {

    @Autowired
    private VictimRepository victimRepository;

    @Autowired
    private InjuryDetailsService injuryDetailsService;

    // Save Death Case
    public void saveDeathCase(Victim victim, DeathDetails deathDetails) {

        // Handle file upload for death certificate
        MultipartFile deathCertificateFile = deathDetails.getDeathCertificateFile(); // Get the file from the object
        if (deathCertificateFile != null && !deathCertificateFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(deathCertificateFile.getOriginalFilename()));
            String uploadDir = "uploads/death-certificates/";
            try {
                FileUploadUtil.saveFile(uploadDir, fileName, deathCertificateFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            deathDetails.setDeathCertificatePath(uploadDir + fileName);
        }

        //Handle photo upload for death photo
        MultipartFile deathPhotoFile = deathDetails.getDeathPhotoFile();
        if(deathPhotoFile != null && !deathPhotoFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(deathPhotoFile.getOriginalFilename()));
            String uploadDir = "uploads/death-photos/";
            try{
                FileUploadUtil.saveFile(uploadDir, fileName, deathPhotoFile);
            }catch (IOException e){
                throw new RuntimeException(e);
            }
            deathDetails.setDeathPhotoPath(uploadDir + fileName);
        }

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

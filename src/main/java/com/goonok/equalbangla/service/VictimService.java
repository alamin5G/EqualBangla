package com.goonok.equalbangla.service;

import com.goonok.equalbangla.model.*;
import com.goonok.equalbangla.repository.TaskRepository;
import com.goonok.equalbangla.repository.VictimRepository;
import com.goonok.equalbangla.specification.VictimSpecification;
import com.goonok.equalbangla.util.FileUploadUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional
public class VictimService {

    @Autowired
    private VictimRepository victimRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private AdminService adminService;


    public Page<Victim> filterVictims(
            String fullName,
            List<String> incidentType,
            LocalDate startDate,
            LocalDate endDate,
            List<String> district,
            String policeStation,
            Integer ageFrom,
            Integer ageTo,
            List<String> gender,
            String occupation,
            String verificationStatus,
            int page,
            int size,
            String sortField,
            String sortDir) {

        // Create a pageable object for pagination and sorting
        Pageable pageable = PageRequest.of(page, size,
                sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending());

        // Delegate filtering to VictimRepository with the criteria built in VictimSpecification
        return victimRepository.findAll(
                VictimSpecification.filterByCriteria(
                        fullName, incidentType, startDate, endDate, district, policeStation,
                        ageFrom, ageTo, gender, occupation, verificationStatus),
                pageable
        );
    }

    // Method to fetch victims based on verification status (PENDING, VERIFIED, REJECTED, Injured, Missing, Death)
    public Page<Victim> getVictimsByStatus(String status, int page, int size, String sortField, String sortDir) {
        Pageable pageable = PageRequest.of(page, size, sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending());
        if (status != null && !status.isEmpty()) {
            switch (status) {
                case "Injured":
                    return victimRepository.findByIncidentType("Injured", pageable);
                case "Missing":
                    return victimRepository.findByIncidentType("Missing", pageable);
                case "Death":
                    return victimRepository.findByIncidentType("Death", pageable);
                case "Verified":
                    return victimRepository.findByVerificationStatus("1", pageable);
                case "Rejected":
                    return victimRepository.findByVerificationStatus("0", pageable);
                case "Pending":
                    return victimRepository.findByVerificationStatus("2", pageable);
                default:
                    return victimRepository.findAll(pageable);
            }
        }
        return victimRepository.findAll(pageable);
    }

    // Method to update the verification status
    public void updateVerificationStatus(Long victimId, String status) {
        Victim victim = victimRepository.findById(victimId).orElseThrow(() -> new RuntimeException("Victim not found"));
        victim.setVerificationStatus(status);
        victimRepository.save(victim);  // Persist the updated status
    }

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
        victim.getDeathDetails().setVictim(victim);
        victimRepository.save(victim);


    }

    // Save Missing Case
    public void saveMissingCase(Victim victim, MissingDetails missingDetails) {

        // Handle file upload for death certificate
        MultipartFile missingPhotographFile = missingDetails.getMissingPhotographFile(); // Get the file from the object
        if (missingPhotographFile != null && !missingPhotographFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(missingPhotographFile.getOriginalFilename()));
            String uploadDir = "uploads/missing-person-photographs/";
            try {
                FileUploadUtil.saveFile(uploadDir, fileName, missingPhotographFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            missingDetails.setMissingPhotographPath(uploadDir + fileName);
        }

        //Handle photo upload for death photo
        MultipartFile missingReportFile = missingDetails.getPoliceReportFile();
        if(missingReportFile != null && !missingReportFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(missingReportFile.getOriginalFilename()));
            String uploadDir = "uploads/missing-person-police-reports/";
            try{
                FileUploadUtil.saveFile(uploadDir, fileName, missingReportFile);
            }catch (IOException e){
                throw new RuntimeException(e);
            }
            missingDetails.setPoliceReportPath(uploadDir + fileName);
        }

        victim.setMissingDetails(missingDetails);
        victim.getMissingDetails().setVictim(victim);
        victimRepository.save(victim);
    }


    public void saveInjuredCase(Victim victim, InjuryDetails injuryDetails) {
        // You don't need to separately save injuryDetails or contactPerson if cascading is set up

        // Handle file upload for death certificate
        MultipartFile injuredPersonPhotographFile = injuryDetails.getInjuredPersonPhotographFile(); // Get the file from the object
        if (injuredPersonPhotographFile != null && !injuredPersonPhotographFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(injuredPersonPhotographFile.getOriginalFilename()));
            String uploadDir = "uploads/injured-person-photographs/";
            try {
                FileUploadUtil.saveFile(uploadDir, fileName, injuredPersonPhotographFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            injuryDetails.setInjuredPersonPhotographPath(uploadDir + fileName);
        }

        //Handle photo upload for death photo
        MultipartFile medicalReportFile = injuryDetails.getMedicalReportFile();
        if(medicalReportFile != null && !medicalReportFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(medicalReportFile.getOriginalFilename()));
            String uploadDir = "uploads/injured-person-medical-reports/";
            try{
                FileUploadUtil.saveFile(uploadDir, fileName, medicalReportFile);
            }catch (IOException e){
                throw new RuntimeException(e);
            }
            injuryDetails.setMedicalReportPath(uploadDir + fileName);
        }

        victim.setInjuryDetails(injuryDetails);
        // Persist the victim, which will automatically persist the associated entities
        victim.getInjuryDetails().setVictim(victim);
        victimRepository.save(victim);
    }

    // Other service methods...

    public long countVictimsByIncidentType(String incidentType) {
        return victimRepository.countByIncidentType(incidentType);
    }

    public Victim getVictimById(Long id) {
        return victimRepository.findById(id).orElseThrow(() -> new RuntimeException("Victim not found"));
    }

    public void save(Victim victim) {
        victimRepository.save(victim);
    }

    public List<Victim> getAllVictims(){
        return victimRepository.findAll();
    }

    public List<Victim> getVictimsFromLastWeek() {
        LocalDate today = LocalDate.now();
        LocalDate oneWeekAgo = today.minusWeeks(1);
        return victimRepository.findVictimsBetweenDates(oneWeekAgo, today);
    }


    //used in AdminVictimController /export/csv
    public List<Victim> getFilteredVictims(
            String fullName,
            List<String> incidentType,
            LocalDate startDate,
            LocalDate endDate,
            List<String> district,
            String policeStation,
            Integer ageFrom,
            Integer ageTo,
            List<String> gender,
            String occupation,
            String verificationStatus) {

        // Reuse the specification filtering logic but fetch all records
        return victimRepository.findAll(
                VictimSpecification.filterByCriteria(
                        fullName, incidentType, startDate, endDate, district, policeStation,
                        ageFrom, ageTo, gender, occupation, verificationStatus));
    }

//update verification status and send confirmation email
    public void verifyVictim(Long id, String verificationStatus, String verificationRemarks) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = "system";
        if (authentication != null && authentication.isAuthenticated()) {
            username = authentication.getName();
        }
        Victim victim = getVictimById(id);
        victim.setVerificationStatus(verificationStatus);
        victim.setVerificationRemarks(verificationRemarks);
        victim.setUpdatedBy(username);
        victimRepository.save(victim);

        // Send email to the victim
        String subject = "Your Report Verification Status";
        String body = "Dear " + victim.getFullName() + ",\n\nYour report has been verified with status: " + verificationStatus
                + ".\nRemarks: " + verificationRemarks + "\n\nThank you.";
        emailService.sendVerificationEmail(victim.getEmail(), subject, body);
    }


    // Full-Text Search by keyword
    public List<Victim> searchVictims(String keyword) {
        return victimRepository.searchByKeyword(keyword);
    }

    //advance search
    public List<Victim> advancedSearchList(String fullName, String incidentType, String startDate, String endDate, String region, String severity) {
        LocalDate start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : null;
        LocalDate end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate) : null;

        return victimRepository.findAll(VictimSpecification.advancedSearchByCriteria(fullName, incidentType, start, end, region, severity));
    }

    // Updated advanced search with pagination
    public Page<Victim> advancedSearch(
            String fullName,
            String incidentType,
            LocalDate startDate,
            LocalDate endDate,
            String region,
            String severity,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        // Delegate the search to the repository using VictimSpecification
        return victimRepository.findAll(
                VictimSpecification.advancedSearchByCriteria(
                        fullName, incidentType, startDate, endDate, region, severity), pageable);
    }



    ///task assign to admin
    public Victim assignCaseToAdmin(Long victimId, Long adminId) {
        Victim victim = victimRepository.findById(victimId).orElseThrow(() -> new RuntimeException("Victim not found"));
        Admin admin = adminService.getAdminById(adminId).orElseThrow(() -> new RuntimeException("Admin not found"));

        victim.setAssignedAdmin(admin);
        victim.setCaseStatus("IN_REVIEW");
        return victimRepository.save(victim);
    }

    public Victim updateCaseStatus(Long victimId, String status) {
        Victim victim = victimRepository.findById(victimId).orElseThrow(() -> new RuntimeException("Victim not found"));
        victim.setCaseStatus(status);
        return victimRepository.save(victim);
    }

    public Task createTaskForCase(Long victimId, Long adminId, String description) {
        Victim victim = victimRepository.findById(victimId).orElseThrow(() -> new RuntimeException("Victim not found"));
        Admin admin = adminService.getAdminById(adminId).orElseThrow(() -> new RuntimeException("Admin not found"));

        Task task = new Task();
        task.setVictim(victim);
        task.setAssignedAdmin(admin);
        task.setDescription(description);
        task.setTaskStatus("PENDING");

        return taskRepository.save(task);
    }

    public List<Task> getTasksForAdmin(Admin admin) {
        return taskRepository.findByAssignedAdmin(admin);
    }


}

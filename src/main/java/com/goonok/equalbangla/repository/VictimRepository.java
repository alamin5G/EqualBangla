package com.goonok.equalbangla.repository;

import com.goonok.equalbangla.model.Victim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VictimRepository extends JpaRepository<Victim, Long>, JpaSpecificationExecutor<Victim> {

    @Query("SELECT v FROM Victim v WHERE v.incidentType = :incidentType AND (:startDate IS NULL OR v.incidentDate >= :startDate) AND (:endDate IS NULL OR v.incidentDate <= :endDate)")
    List<Victim> findByIncidentTypeAndDateRange(String incidentType, LocalDate startDate, LocalDate endDate);

    @Query("SELECT v FROM Victim v WHERE v.verificationStatus = :status")
    List<Victim> findByVerificationStatus(String status);

    // Use Spring Data JPA's method name conventions
    long countByIncidentType(String incidentType);

    // If you need to customize it further, you can use the @Query annotation
    @Query("SELECT COUNT(v) FROM Victim v WHERE v.incidentType = :incidentType")
    long findCountByIncidentType(String incidentType);

    // Custom query method to fetch victims by verification status
    Page<Victim> findByVerificationStatus(String verificationStatus, Pageable pageable);

    Page<Victim> findByIncidentType(String incidentType, Pageable pageable);


}

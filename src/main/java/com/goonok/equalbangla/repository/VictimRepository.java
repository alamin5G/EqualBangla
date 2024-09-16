package com.goonok.equalbangla.repository;

import com.goonok.equalbangla.model.Victim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface VictimRepository extends JpaRepository<Victim, Long>, JpaSpecificationExecutor<Victim> {

    @Query("SELECT v FROM Victim v WHERE v.incidentType = :incidentType AND (:startDate IS NULL OR v.incidentDate >= :startDate) AND (:endDate IS NULL OR v.incidentDate <= :endDate)")
    List<Victim> findByIncidentTypeAndDateRange(String incidentType, LocalDate startDate, LocalDate endDate);

    @Query("SELECT v FROM Victim v WHERE v.verificationStatus = :status")
    List<Victim> findByVerificationStatus(String status);

    // Use Spring Data JPA's method name conventions
    long countByIncidentType(String incidentType);

    @Query("SELECT v.incidentType, COUNT(v) FROM Victim v GROUP BY v.incidentType")
    List<Object[]> countByIncidentType();

    // If you need to customize it further, you can use the @Query annotation
    @Query("SELECT COUNT(v) FROM Victim v WHERE v.incidentType = :incidentType")
    long findCountByIncidentType(String incidentType);

    // Custom query method to fetch victims by verification status
    Page<Victim> findByVerificationStatus(String verificationStatus, Pageable pageable);

    Page<Victim> findByIncidentType(String incidentType, Pageable pageable);

    // Count cases by incident type (e.g., Injured, Missing, Death)
    @Query("SELECT v.incidentType, COUNT(v) FROM Victim v GROUP BY v.incidentType")
    List<Object[]> countCasesByIncidentType();


    // Count verified and unverified reports
    @Query("SELECT v.verificationStatus, COUNT(v) FROM Victim v GROUP BY v.verificationStatus")
    Map<String, Long> countVerificationStatus();

    // Query to get counts grouped by month (cases over time)
    @Query("SELECT FUNCTION('MONTH', v.incidentDate), COUNT(v) FROM Victim v GROUP BY FUNCTION('MONTH', v.incidentDate)")
    List<Object[]> countCasesOverTime();

    // Query to get counts grouped by district (cases by location)
    @Query("SELECT v.district, COUNT(v) FROM Victim v GROUP BY v.district")
    List<Object[]> countCasesByLocation();

    // Query to get distinct locations
    @Query("SELECT DISTINCT v.district FROM Victim v")
    List<String> getDistinctLocations();

    // Query to count verified cases
    @Query("SELECT COUNT(v) FROM Victim v WHERE v.verificationStatus = '1'")
    Long countVerified();

    // Query to count unverified cases
    @Query("SELECT COUNT(v) FROM Victim v WHERE v.verificationStatus = '2'")
    Long countUnverified();

    // Query to count unverified cases
    @Query("SELECT COUNT(v) FROM Victim v WHERE v.verificationStatus = '0'")
    Long countRejectedByVerificationStatus();

    @Query("SELECT v FROM Victim v WHERE v.incidentDate BETWEEN :startDate AND :endDate")
    List<Victim> findVictimsBetweenDates(LocalDate startDate, LocalDate endDate);


}

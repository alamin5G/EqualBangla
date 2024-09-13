package com.goonok.equalbangla.repository;

import com.goonok.equalbangla.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username);

    @Query("SELECT v.incidentType, COUNT(v) FROM Victim v GROUP BY v.incidentType")
    List<Object[]> countByIncidentType();
}

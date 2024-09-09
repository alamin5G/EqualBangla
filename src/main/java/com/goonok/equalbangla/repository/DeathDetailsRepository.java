package com.goonok.equalbangla.repository;


import com.goonok.equalbangla.model.DeathDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeathDetailsRepository extends JpaRepository<DeathDetails, Long> {
}
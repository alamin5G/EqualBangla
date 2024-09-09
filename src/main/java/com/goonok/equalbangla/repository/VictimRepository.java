package com.goonok.equalbangla.repository;

import com.goonok.equalbangla.model.Victim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VictimRepository extends JpaRepository<Victim, Long> {
}
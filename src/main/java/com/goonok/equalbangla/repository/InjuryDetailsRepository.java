package com.goonok.equalbangla.repository;


import com.goonok.equalbangla.model.InjuryDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InjuryDetailsRepository extends JpaRepository<InjuryDetails, Long> {
}
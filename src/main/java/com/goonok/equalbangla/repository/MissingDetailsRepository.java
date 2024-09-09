package com.goonok.equalbangla.repository;


import com.goonok.equalbangla.model.MissingDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissingDetailsRepository extends JpaRepository<MissingDetails, Long> {
}
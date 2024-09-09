package com.goonok.equalbangla.repository;

import com.goonok.equalbangla.model.ContactPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactPersonRepository extends JpaRepository<ContactPerson, Long> {
}
package com.goonok.equalbangla.repository;

import com.goonok.equalbangla.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username);

    List<Admin> findAllByEnabled(boolean enabled);
    List<Admin> findAllByCanManageAdmins(boolean canManageAdmin);

}

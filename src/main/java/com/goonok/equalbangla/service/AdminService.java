package com.goonok.equalbangla.service;

import com.goonok.equalbangla.model.Admin;
import com.goonok.equalbangla.power_admin.CustomAdminDetails;
import com.goonok.equalbangla.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));

        /*return new org.springframework.security.core.userdetails.User(
                admin.getUsername(),
                admin.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );
                */

        // Check if admin is enabled before allowing login
        if (!admin.isEnabled()) {
            throw new UsernameNotFoundException("Admin account is disabled.");
        }

        // You can customize the return to include the canManageAdmins flag if necessary
        return new CustomAdminDetails(admin);

    }

    public void createAdmin(String username, String password) {
        if (adminRepository.findByUsername(username).isEmpty()) {
            String createdBy = SecurityContextHolder.getContext().getAuthentication().getName();

            Admin admin = new Admin();
            admin.setUsername(username);
            admin.setPassword(new BCryptPasswordEncoder().encode(password));  // Encrypt the password
            admin.setCreatedBy(createdBy);  // Set the 'created_by' field
            admin.setCanManageAdmins(false); // Default to not managing admins (can be set later)
            admin.setEnabled(true);  // Set enabled to true by default

            adminRepository.save(admin);
        }
    }

    // Method to update the canManageAdmins flag
    public void updateCanManageAdmins(String username, boolean canManageAdmins) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));

        admin.setCanManageAdmins(canManageAdmins);
        adminRepository.save(admin);
    }

    // Enable or disable an admin
    public void updateAdminStatus(Long id, boolean enabled) {
        Admin admin = adminRepository.findById(id).orElseThrow(() -> new RuntimeException("Admin not found"));
        admin.setEnabled(enabled);
        adminRepository.save(admin);
    }

    public void updateManageAdminStatus(Long id, boolean enabled) {
        Admin admin = adminRepository.findById(id).orElseThrow(() -> new RuntimeException("Admin not found"));
        admin.setCanManageAdmins(enabled);
        adminRepository.save(admin);
    }

    public void updateAdminPassword(Long id, String newPassword) {
        Admin admin = adminRepository.findById(id).orElseThrow(() -> new RuntimeException("Admin not found"));
        admin.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        adminRepository.save(admin);
    }

    public Optional<Admin> findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    public Optional<Admin> getAdminById(Long id) {
        return adminRepository.findById(id);
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public List<Admin> findAllByStatus(String status) {
        if (status != null && !status.isEmpty()) {
            switch (status) {
                case "enabled":
                    return adminRepository.findAllByEnabled(true);
                case "disabled":
                    return adminRepository.findAllByEnabled(false);
                case "power":
                    return adminRepository.findAllByCanManageAdmins(true);
                case "normal":
                    return adminRepository.findAllByCanManageAdmins(false);
                default:
                   return adminRepository.findAll();
            }
        }else {
            return adminRepository.findAll();
        }
    }
}
package com.goonok.equalbangla.service;

import com.goonok.equalbangla.model.Admin;
import com.goonok.equalbangla.power_admin.CustomAdminDetails;
import com.goonok.equalbangla.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
    private EmailService emailService;

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

    public void createAdmin(String username, String password, String email) {
        if (adminRepository.findByUsername(username).isEmpty()) {
            String createdBy = "system";
            Admin admin = new Admin(); //initialize first because, there I need to set some logic
            admin.setCanManageAdmins(true); // if it is the first user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String authName = authentication.getName();
                if (authName != null && !authName.isEmpty()) {
                    createdBy = authName; // if there is user then admin can manage another admins will be updated as false
                    admin.setCanManageAdmins(false); // Default to not managing admins (can be set later)
                }
            }


            admin.setUsername(username);
            admin.setEmail(email);
            admin.setPassword(new BCryptPasswordEncoder().encode(password));  // Encrypt the password
            admin.setCreatedBy(createdBy);  // Set the 'created_by' field
            admin.setUpdatedBy(createdBy);  // Set the 'updated_by' field as it is first time updated

            admin.setEnabled(false);  // Set enabled to false by default due to approval

            adminRepository.save(admin);
        }
    }


    // Enable or disable an admin
    public void updateAdminStatus(Long id, boolean enabled) {
        Admin admin = adminRepository.findById(id).orElseThrow(() -> new RuntimeException("Admin not found"));
        String updatedBy = SecurityContextHolder.getContext().getAuthentication().getName();
        admin.setEnabled(enabled);
        admin.setUpdatedBy(updatedBy);
        adminRepository.save(admin);
    }

    public void updateManageAdminStatus(Long id, boolean enabled) {
        Admin admin = adminRepository.findById(id).orElseThrow(() -> new RuntimeException("Admin not found"));
        String updatedBy = SecurityContextHolder.getContext().getAuthentication().getName();
        admin.setCanManageAdmins(enabled);
        admin.setUpdatedBy(updatedBy);
        adminRepository.save(admin);
    }

    public void updateAdminPassword(Long id, String newPassword) {
        Admin admin = adminRepository.findById(id).orElseThrow(() -> new RuntimeException("Admin not found"));
        String updatedBy = SecurityContextHolder.getContext().getAuthentication().getName();
        admin.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        admin.setUpdatedBy(updatedBy);
        adminRepository.save(admin);

        String body = "Hello, " + admin.getUsername() + "!\n Your password has been changed by the admin (" + updatedBy + "). Your new password is: " + newPassword;
        emailService.sendEmail(admin.getEmail(), "Your password has been changed", body);
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
package com.goonok.equalbangla.power_admin;

import com.goonok.equalbangla.model.Admin;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;

@Getter
public class CustomAdminDetails extends org.springframework.security.core.userdetails.User {

    private final Admin admin;

    public CustomAdminDetails(Admin admin) {
        super(admin.getUsername(), admin.getPassword(), admin.isEnabled(), true, true, true, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))); // Set roles/authorities if needed
        this.admin = admin;
    }

    public boolean canManageAdmins() {
        return admin.isCanManageAdmins();
    }

    @Override
    public boolean isEnabled() {
        return admin.isEnabled();
    }

}

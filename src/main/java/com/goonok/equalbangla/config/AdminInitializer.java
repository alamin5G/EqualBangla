package com.goonok.equalbangla.config;
import com.goonok.equalbangla.service.AdminService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final AdminService adminService;

    public AdminInitializer(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        adminService.createAdmin("alamin", "alaminEqualBangladesh");
    }
}

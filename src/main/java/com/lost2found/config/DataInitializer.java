package com.lost2found.config;

import com.lost2found.entity.Role;
import com.lost2found.entity.RoleName;
import com.lost2found.repository.RoleRepository;
import com.lost2found.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds required initial system roles into database on application startup.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        for (RoleName roleName : RoleName.values()) {
            if (!roleRepository.existsByName(roleName)) {
                Role role = new Role(roleName);
                roleRepository.save(role);
                log.info("Initialized default role in database: {}", roleName);
            }
        }

        if (!userRepository.existsByUsername("student")) {
            com.lost2found.entity.User demoUser = new com.lost2found.entity.User(
                    "Sai Teja",
                    "student",
                    "student@college.edu",
                    passwordEncoder.encode("Password123!"),
                    "+91 9876543210"
            );
            demoUser.setRoles(java.util.Collections.singleton(RoleName.ROLE_USER.name()));
            userRepository.save(demoUser);
            log.info("Initialized default demo student account: student / student@college.edu");
        }
    }
}

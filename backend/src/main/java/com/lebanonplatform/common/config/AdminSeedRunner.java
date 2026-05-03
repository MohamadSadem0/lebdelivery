package com.lebanonplatform.common.config;

import com.lebanonplatform.modules.roles.domain.EntityType;
import com.lebanonplatform.modules.roles.domain.PlatformRole;
import com.lebanonplatform.modules.roles.domain.UserRole;
import com.lebanonplatform.modules.roles.repository.UserRoleRepository;
import com.lebanonplatform.modules.users.domain.User;
import com.lebanonplatform.modules.users.domain.UserStatus;
import com.lebanonplatform.modules.users.repository.UserRepository;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("dev")
public class AdminSeedRunner implements ApplicationRunner {

    private final boolean enabled;
    private final String phone;
    private final String email;
    private final String password;
    private final String fullName;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeedRunner(
            @Value("${app.admin-seed.enabled}") boolean enabled,
            @Value("${app.admin-seed.phone}") String phone,
            @Value("${app.admin-seed.email}") String email,
            @Value("${app.admin-seed.password}") String password,
            @Value("${app.admin-seed.full-name}") String fullName,
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.enabled = enabled;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!enabled) {
            return;
        }

        if (phone == null || phone.isBlank() || password == null || password.length() < 8) {
            throw new IllegalStateException("Admin seed is enabled but ADMIN_SEED_PHONE or ADMIN_SEED_PASSWORD is invalid.");
        }

        User user = userRepository.findByPhoneNumber(phone).orElseGet(() -> {
            User created = new User();
            created.setPhoneNumber(phone);
            created.setEmail(email == null || email.isBlank() ? null : email);
            created.setFullName(fullName);
            created.setPasswordHash(passwordEncoder.encode(password));
            created.setStatus(UserStatus.ACTIVE);
            created.setActive(true);
            return userRepository.save(created);
        });

        boolean hasAdmin = userRoleRepository.findByUserId(user.getId()).stream()
                .anyMatch(role -> role.getRole() == PlatformRole.ADMIN
                        && role.getEntityType() == EntityType.PLATFORM
                        && Objects.isNull(role.getEntityId()));

        if (!hasAdmin) {
            UserRole adminRole = new UserRole();
            adminRole.setUser(user);
            adminRole.setRole(PlatformRole.ADMIN);
            adminRole.setEntityType(EntityType.PLATFORM);
            adminRole.setEntityId(null);
            userRoleRepository.save(adminRole);
        }
    }
}

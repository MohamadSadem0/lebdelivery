package com.lebanonplatform.modules.users.application;

import com.lebanonplatform.common.audit.AuditService;
import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.common.security.UserPrincipal;
import com.lebanonplatform.modules.users.domain.User;
import com.lebanonplatform.modules.users.dto.request.UpdateCurrentUserRequest;
import com.lebanonplatform.modules.users.dto.response.UserProfileResponse;
import com.lebanonplatform.modules.users.repository.UserRepository;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

    private final UserRepository userRepository;
    private final AuditService auditService;

    public UserProfileService(UserRepository userRepository, AuditService auditService) {
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentProfile(Authentication authentication) {
        return toResponse(getAuthenticatedUser(authentication));
    }

    @Transactional
    public UserProfileResponse updateCurrentProfile(Authentication authentication, UpdateCurrentUserRequest request) {
        User user = getAuthenticatedUser(authentication);

        if (request.fullName() != null) {
            String fullName = request.fullName().trim();
            if (fullName.isBlank()) {
                throw new BaseApplicationException("VALIDATION_ERROR", "Full name cannot be blank.");
            }
            user.setFullName(fullName);
        }

        if (request.phone() != null) {
            String phone = request.phone().trim();
            if (phone.isBlank()) {
                throw new BaseApplicationException("VALIDATION_ERROR", "Phone cannot be blank.");
            }
            if (userRepository.existsByPhoneNumberAndIdNot(phone, user.getId())) {
                throw new BaseApplicationException("PHONE_ALREADY_REGISTERED", "Phone number is already registered.");
            }
            user.setPhoneNumber(phone);
        }

        if (request.email() != null) {
            String email = request.email().trim();
            if (email.isBlank()) {
                user.setEmail(null);
            } else {
                if (userRepository.existsByEmailAndIdNot(email, user.getId())) {
                    throw new BaseApplicationException("EMAIL_ALREADY_REGISTERED", "Email is already registered.");
                }
                user.setEmail(email);
            }
        }

        User saved = userRepository.save(user);
        auditService.record(saved.getId(), "USER_PROFILE_UPDATED", "USER", saved.getId(), "{}");
        return toResponse(saved);
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BaseApplicationException("UNAUTHENTICATED", "Authentication is required.");
        }

        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new BaseApplicationException("USER_NOT_FOUND", "User was not found."));
    }

    private UserProfileResponse toResponse(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getStatus(),
                user.getLastLoginAt(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}

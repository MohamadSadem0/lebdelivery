package com.lebanonplatform.modules.auth.application;

import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.common.security.UserPrincipal;
import com.lebanonplatform.common.security.jwt.JwtService;
import com.lebanonplatform.modules.auth.domain.RefreshToken;
import com.lebanonplatform.modules.auth.dto.request.ActiveRoleRequest;
import com.lebanonplatform.modules.auth.dto.request.LoginRequest;
import com.lebanonplatform.modules.auth.dto.request.LogoutRequest;
import com.lebanonplatform.modules.auth.dto.request.RefreshTokenRequest;
import com.lebanonplatform.modules.auth.dto.request.RegisterRequest;
import com.lebanonplatform.modules.auth.dto.response.ActiveRoleResponse;
import com.lebanonplatform.modules.auth.dto.response.AuthResponse;
import com.lebanonplatform.modules.auth.dto.response.CurrentUserResponse;
import com.lebanonplatform.modules.auth.dto.response.RoleResponse;
import com.lebanonplatform.modules.auth.dto.response.UserSummaryResponse;
import com.lebanonplatform.modules.auth.repository.RefreshTokenRepository;
import com.lebanonplatform.modules.clients.domain.ClientProfile;
import com.lebanonplatform.modules.clients.repository.ClientProfileRepository;
import com.lebanonplatform.modules.roles.domain.EntityType;
import com.lebanonplatform.modules.roles.domain.PlatformRole;
import com.lebanonplatform.modules.roles.domain.UserRole;
import com.lebanonplatform.modules.roles.repository.UserRoleRepository;
import com.lebanonplatform.modules.users.domain.User;
import com.lebanonplatform.modules.users.domain.UserStatus;
import com.lebanonplatform.modules.users.repository.UserRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final UserRoleRepository userRoleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenHashService tokenHashService;
    private final long refreshExpirationDays;

    public AuthService(
            UserRepository userRepository,
            ClientProfileRepository clientProfileRepository,
            UserRoleRepository userRoleRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            TokenHashService tokenHashService,
            @Value("${app.jwt.refresh-expiration-days}") long refreshExpirationDays
    ) {
        this.userRepository = userRepository;
        this.clientProfileRepository = clientProfileRepository;
        this.userRoleRepository = userRoleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenHashService = tokenHashService;
        this.refreshExpirationDays = refreshExpirationDays;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByPhoneNumber(request.phone())) {
            throw new BaseApplicationException("PHONE_ALREADY_REGISTERED", "Phone number is already registered.");
        }

        if (request.email() != null && !request.email().isBlank() && userRepository.existsByEmail(request.email())) {
            throw new BaseApplicationException("EMAIL_ALREADY_REGISTERED", "Email is already registered.");
        }

        User user = new User();
        user.setFullName(request.fullName());
        user.setPhoneNumber(request.phone());
        user.setEmail(normalizeOptional(request.email()));
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setActive(true);
        user.setStatus(UserStatus.ACTIVE);
        user = userRepository.save(user);

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setUser(user);
        clientProfile = clientProfileRepository.save(clientProfile);

        UserRole clientRole = new UserRole();
        clientRole.setUser(user);
        clientRole.setRole(PlatformRole.CLIENT);
        clientRole.setEntityType(EntityType.CLIENT);
        clientRole.setEntityId(clientProfile.getId());
        userRoleRepository.save(clientRole);

        return issueAuthResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByPhoneNumber(request.phone())
                .orElseThrow(() -> new BaseApplicationException("INVALID_CREDENTIALS", "Invalid phone or password."));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BaseApplicationException("INVALID_CREDENTIALS", "Invalid phone or password.");
        }

        assertUserCanLogin(user);
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        return issueAuthResponse(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHashService.sha256(request.refreshToken()))
                .orElseThrow(() -> new BaseApplicationException("INVALID_REFRESH_TOKEN", "Refresh token is invalid."));

        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BaseApplicationException("INVALID_REFRESH_TOKEN", "Refresh token is invalid or expired.");
        }

        User user = refreshToken.getUser();
        assertUserCanLogin(user);

        refreshToken.setRevokedAt(Instant.now());
        refreshTokenRepository.save(refreshToken);

        return issueAuthResponse(user);
    }

    @Transactional
    public void logout(LogoutRequest request) {
        if (request.refreshToken() == null || request.refreshToken().isBlank()) {
            return;
        }

        refreshTokenRepository.findByTokenHash(tokenHashService.sha256(request.refreshToken()))
                .filter(token -> !token.isRevoked())
                .ifPresent(token -> {
                    token.setRevokedAt(Instant.now());
                    refreshTokenRepository.save(token);
                });
    }

    @Transactional(readOnly = true)
    public CurrentUserResponse getCurrentUser(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        List<RoleResponse> roles = userRoleRepository.findByUserOrderByRoleAsc(user).stream()
                .map(this::toRoleResponse)
                .toList();

        return new CurrentUserResponse(
                user.getId(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getStatus(),
                roles
        );
    }

    @Transactional(readOnly = true)
    public ActiveRoleResponse selectActiveRole(Authentication authentication, ActiveRoleRequest request) {
        User user = getAuthenticatedUser(authentication);
        UserRole role = userRoleRepository.findByUserId(user.getId()).stream()
                .filter(candidate -> candidate.getRole() == request.role())
                .filter(candidate -> candidate.getEntityType() == request.entityType())
                .filter(candidate -> Objects.equals(candidate.getEntityId(), request.entityId()))
                .findFirst()
                .orElseThrow(() -> new BaseApplicationException("ROLE_NOT_ASSIGNED", "The requested role is not assigned to this user."));

        return new ActiveRoleResponse(toRoleResponse(role));
    }

    private AuthResponse issueAuthResponse(User user) {
        List<PlatformRole> roles = userRoleRepository.findByUserId(user.getId()).stream()
                .map(UserRole::getRole)
                .distinct()
                .toList();
        String accessToken = jwtService.createAccessToken(user, roles);
        String refreshToken = createRefreshToken(user);
        return new AuthResponse(toUserSummary(user, roles), accessToken, refreshToken);
    }

    private String createRefreshToken(User user) {
        String rawToken = tokenHashService.newRefreshToken();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(tokenHashService.sha256(rawToken));
        refreshToken.setExpiresAt(Instant.now().plus(refreshExpirationDays, ChronoUnit.DAYS));
        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BaseApplicationException("UNAUTHENTICATED", "Authentication is required.");
        }

        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new BaseApplicationException("USER_NOT_FOUND", "User was not found."));
    }

    private UserSummaryResponse toUserSummary(User user, List<PlatformRole> roles) {
        return new UserSummaryResponse(
                user.getId(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getStatus(),
                roles.stream().map(Enum::name).toList()
        );
    }

    private RoleResponse toRoleResponse(UserRole role) {
        return new RoleResponse(role.getId(), role.getRole(), role.getEntityType(), role.getEntityId());
    }

    private void assertUserCanLogin(User user) {
        if (!user.isActive() || user.getStatus() != UserStatus.ACTIVE) {
            throw new BaseApplicationException("USER_INACTIVE", "User is not active.");
        }
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}

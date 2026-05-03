package com.lebanonplatform.modules.notifications.application;

import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.common.security.UserPrincipal;
import com.lebanonplatform.modules.notifications.domain.Notification;
import com.lebanonplatform.modules.notifications.dto.response.NotificationResponse;
import com.lebanonplatform.modules.notifications.dto.response.UnreadNotificationCountResponse;
import com.lebanonplatform.modules.notifications.repository.NotificationRepository;
import com.lebanonplatform.modules.users.domain.User;
import com.lebanonplatform.modules.users.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void create(UUID userId, String title, String body) {
        if (userId == null) {
            return;
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return;
        }
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setBody(body);
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> list(Authentication authentication, int page, int size) {
        UUID userId = currentUserId(authentication);
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId, PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50))).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UnreadNotificationCountResponse unreadCount(Authentication authentication) {
        return new UnreadNotificationCountResponse(notificationRepository.countByUser_IdAndReadAtIsNull(currentUserId(authentication)));
    }

    @Transactional
    public NotificationResponse markRead(Authentication authentication, UUID notificationId) {
        UUID userId = currentUserId(authentication);
        Notification notification = notificationRepository.findByIdAndUser_Id(notificationId, userId)
                .orElseThrow(() -> new BaseApplicationException("NOTIFICATION_NOT_FOUND", "Notification was not found."));
        if (notification.getReadAt() == null) {
            notification.setReadAt(Instant.now());
        }
        return toResponse(notificationRepository.save(notification));
    }

    @Transactional
    public UnreadNotificationCountResponse markAllRead(Authentication authentication) {
        UUID userId = currentUserId(authentication);
        Instant now = Instant.now();
        notificationRepository.findByUser_IdAndReadAtIsNull(userId).forEach(notification -> notification.setReadAt(now));
        return new UnreadNotificationCountResponse(0);
    }

    public NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getUser().getId(),
                notification.getTitle(),
                notification.getBody(),
                notification.getReadAt(),
                notification.getCreatedAt()
        );
    }

    private UUID currentUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BaseApplicationException("UNAUTHENTICATED", "Authentication is required.");
        }
        return principal.getId();
    }
}

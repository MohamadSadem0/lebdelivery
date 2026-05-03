package com.lebanonplatform.modules.notifications.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.notifications.application.NotificationService;
import com.lebanonplatform.modules.notifications.dto.response.NotificationResponse;
import com.lebanonplatform.modules.notifications.dto.response.UnreadNotificationCountResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ApiResponse<List<NotificationResponse>> list(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(notificationService.list(authentication, page, size));
    }

    @GetMapping("/unread-count")
    public ApiResponse<UnreadNotificationCountResponse> unreadCount(Authentication authentication) {
        return ApiResponse.success(notificationService.unreadCount(authentication));
    }

    @PostMapping("/{notificationId}/read")
    public ApiResponse<NotificationResponse> markRead(Authentication authentication, @PathVariable UUID notificationId) {
        return ApiResponse.success(notificationService.markRead(authentication, notificationId));
    }

    @PostMapping("/read-all")
    public ApiResponse<UnreadNotificationCountResponse> markAllRead(Authentication authentication) {
        return ApiResponse.success(notificationService.markAllRead(authentication));
    }
}

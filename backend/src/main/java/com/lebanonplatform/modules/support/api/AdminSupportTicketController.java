package com.lebanonplatform.modules.support.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.support.application.SupportTicketService;
import com.lebanonplatform.modules.support.dto.request.UpdateSupportTicketStatusRequest;
import com.lebanonplatform.modules.support.dto.response.SupportTicketResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/support/tickets")
@PreAuthorize("hasAnyRole('ADMIN','SUPPORT_AGENT')")
public class AdminSupportTicketController {

    private final SupportTicketService supportTicketService;

    public AdminSupportTicketController(SupportTicketService supportTicketService) {
        this.supportTicketService = supportTicketService;
    }

    @GetMapping
    public ApiResponse<List<SupportTicketResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(supportTicketService.listAdmin(page, size));
    }

    @GetMapping("/{ticketId}")
    public ApiResponse<SupportTicketResponse> get(@PathVariable UUID ticketId) {
        return ApiResponse.success(supportTicketService.getAdmin(ticketId));
    }

    @PatchMapping("/{ticketId}/status")
    public ApiResponse<SupportTicketResponse> updateStatus(
            Authentication authentication,
            @PathVariable UUID ticketId,
            @Valid @RequestBody UpdateSupportTicketStatusRequest request
    ) {
        return ApiResponse.success(supportTicketService.updateStatus(authentication, ticketId, request));
    }
}

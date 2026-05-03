package com.lebanonplatform.modules.support.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.support.application.SupportTicketService;
import com.lebanonplatform.modules.support.dto.request.CreateSupportTicketRequest;
import com.lebanonplatform.modules.support.dto.response.SupportTicketResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/support/tickets")
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    public SupportTicketController(SupportTicketService supportTicketService) {
        this.supportTicketService = supportTicketService;
    }

    @PostMapping
    public ApiResponse<SupportTicketResponse> create(Authentication authentication, @Valid @RequestBody CreateSupportTicketRequest request) {
        return ApiResponse.success("Support ticket created.", supportTicketService.create(authentication, request));
    }

    @GetMapping
    public ApiResponse<List<SupportTicketResponse>> list(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(supportTicketService.listMine(authentication, page, size));
    }

    @GetMapping("/{ticketId}")
    public ApiResponse<SupportTicketResponse> get(Authentication authentication, @PathVariable UUID ticketId) {
        return ApiResponse.success(supportTicketService.getMine(authentication, ticketId));
    }
}

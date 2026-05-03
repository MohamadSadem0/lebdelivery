package com.lebanonplatform.modules.receipts.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.receipts.application.ReceiptService;
import com.lebanonplatform.modules.receipts.dto.response.ReceiptResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/client/receipts")
public class ClientReceiptController {

    private final ReceiptService receiptService;

    public ClientReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @GetMapping
    public ApiResponse<List<ReceiptResponse>> listReceipts(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(receiptService.listClientReceipts(authentication, page, size));
    }

    @GetMapping("/{receiptId}")
    public ApiResponse<ReceiptResponse> getReceipt(Authentication authentication, @PathVariable UUID receiptId) {
        return ApiResponse.success(receiptService.getClientReceipt(authentication, receiptId));
    }
}

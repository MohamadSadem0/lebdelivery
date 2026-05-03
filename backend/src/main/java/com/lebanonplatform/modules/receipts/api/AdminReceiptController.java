package com.lebanonplatform.modules.receipts.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.receipts.application.ReceiptService;
import com.lebanonplatform.modules.receipts.dto.response.ReceiptResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/receipts")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReceiptController {

    private final ReceiptService receiptService;

    public AdminReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @GetMapping
    public ApiResponse<List<ReceiptResponse>> listReceipts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(receiptService.listAdminReceipts(page, size));
    }

    @GetMapping("/{receiptId}")
    public ApiResponse<ReceiptResponse> getReceipt(@PathVariable UUID receiptId) {
        return ApiResponse.success(receiptService.getAdminReceipt(receiptId));
    }
}

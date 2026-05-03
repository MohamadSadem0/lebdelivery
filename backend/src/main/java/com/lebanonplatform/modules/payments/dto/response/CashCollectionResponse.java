package com.lebanonplatform.modules.payments.dto.response;

import com.lebanonplatform.modules.receipts.dto.response.ReceiptResponse;

public record CashCollectionResponse(
        PaymentResponse payment,
        ReceiptResponse receipt
) {
}

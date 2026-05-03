package com.lebanonplatform.modules.payments.application;

import com.lebanonplatform.common.audit.AuditService;
import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.common.security.UserPrincipal;
import com.lebanonplatform.modules.drivers.domain.Driver;
import com.lebanonplatform.modules.drivers.repository.DriverRepository;
import com.lebanonplatform.modules.payments.domain.CashSettlement;
import com.lebanonplatform.modules.payments.dto.request.CreateCashSettlementRequest;
import com.lebanonplatform.modules.payments.dto.response.CashSettlementResponse;
import com.lebanonplatform.modules.payments.repository.CashSettlementRepository;
import com.lebanonplatform.modules.payments.repository.PaymentRepository;
import com.lebanonplatform.modules.users.domain.User;
import com.lebanonplatform.modules.users.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CashSettlementService {

    private final CashSettlementRepository cashSettlementRepository;
    private final PaymentRepository paymentRepository;
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public CashSettlementService(
            CashSettlementRepository cashSettlementRepository,
            PaymentRepository paymentRepository,
            DriverRepository driverRepository,
            UserRepository userRepository,
            AuditService auditService
    ) {
        this.cashSettlementRepository = cashSettlementRepository;
        this.paymentRepository = paymentRepository;
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<CashSettlementResponse> list(UUID driverId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100), Sort.by(Sort.Direction.DESC, "createdAt"));
        if (driverId != null) {
            return cashSettlementRepository.findByDriver_IdOrderByCreatedAtDesc(driverId, pageRequest).stream()
                    .map(this::toResponse)
                    .toList();
        }
        return cashSettlementRepository.findAll(pageRequest).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CashSettlementResponse create(Authentication authentication, CreateCashSettlementRequest request) {
        User actor = currentUser(authentication);
        Driver driver = driverRepository.findById(request.driverId())
                .orElseThrow(() -> new BaseApplicationException("DRIVER_NOT_FOUND", "Driver was not found."));
        BigDecimal unsettled = unsettledCashForDriver(driver.getId());
        if (request.amount().compareTo(unsettled) > 0) {
            throw new BaseApplicationException("SETTLEMENT_EXCEEDS_UNSETTLED_CASH", "Settlement amount is greater than unsettled cash.");
        }

        CashSettlement settlement = new CashSettlement();
        settlement.setDriver(driver);
        settlement.setAmount(request.amount());
        settlement.setNote(request.note());
        settlement.setCreatedByUser(actor);
        settlement = cashSettlementRepository.save(settlement);
        auditService.record(actor.getId(), "CASH_SETTLEMENT_CREATED", "DRIVER", driver.getId(), "{\"amount\":\"" + request.amount() + "\"}");
        return toResponse(settlement);
    }

    public BigDecimal settledCashForDriver(UUID driverId) {
        return cashSettlementRepository.sumAmountByDriver(driverId);
    }

    public BigDecimal unsettledCashForDriver(UUID driverId) {
        BigDecimal collected = paymentRepository.sumCollectedAmountByDriver(driverId);
        BigDecimal settled = settledCashForDriver(driverId);
        BigDecimal unsettled = collected.subtract(settled);
        return unsettled.signum() < 0 ? BigDecimal.ZERO : unsettled;
    }

    public CashSettlementResponse toResponse(CashSettlement settlement) {
        return new CashSettlementResponse(
                settlement.getId(),
                settlement.getDriver().getId(),
                settlement.getDriver().getUser().getFullName(),
                settlement.getAmount(),
                settlement.getNote(),
                settlement.getCreatedByUser() == null ? null : settlement.getCreatedByUser().getId(),
                settlement.getCreatedByUser() == null ? null : settlement.getCreatedByUser().getFullName(),
                settlement.getCreatedAt()
        );
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BaseApplicationException("UNAUTHENTICATED", "Authentication is required.");
        }
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new BaseApplicationException("USER_NOT_FOUND", "User was not found."));
    }
}

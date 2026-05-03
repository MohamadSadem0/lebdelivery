package com.lebanonplatform.modules.support.application;

import com.lebanonplatform.common.audit.AuditService;
import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.common.security.AuthorizationService;
import com.lebanonplatform.common.security.UserPrincipal;
import com.lebanonplatform.modules.deliveries.domain.Delivery;
import com.lebanonplatform.modules.deliveries.repository.DeliveryRepository;
import com.lebanonplatform.modules.notifications.application.NotificationService;
import com.lebanonplatform.modules.orders.domain.Order;
import com.lebanonplatform.modules.orders.repository.OrderRepository;
import com.lebanonplatform.modules.roles.domain.PlatformRole;
import com.lebanonplatform.modules.servicerequests.domain.ServiceRequest;
import com.lebanonplatform.modules.servicerequests.repository.ServiceRequestRepository;
import com.lebanonplatform.modules.support.domain.SupportTicket;
import com.lebanonplatform.modules.support.dto.request.CreateSupportTicketRequest;
import com.lebanonplatform.modules.support.dto.request.UpdateSupportTicketStatusRequest;
import com.lebanonplatform.modules.support.dto.response.SupportTicketResponse;
import com.lebanonplatform.modules.support.repository.SupportTicketRepository;
import com.lebanonplatform.modules.users.domain.User;
import com.lebanonplatform.modules.users.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final AuthorizationService authorizationService;
    private final NotificationService notificationService;
    private final AuditService auditService;

    public SupportTicketService(
            SupportTicketRepository supportTicketRepository,
            UserRepository userRepository,
            OrderRepository orderRepository,
            DeliveryRepository deliveryRepository,
            ServiceRequestRepository serviceRequestRepository,
            AuthorizationService authorizationService,
            NotificationService notificationService,
            AuditService auditService
    ) {
        this.supportTicketRepository = supportTicketRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.deliveryRepository = deliveryRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.authorizationService = authorizationService;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }

    @Transactional
    public SupportTicketResponse create(Authentication authentication, CreateSupportTicketRequest request) {
        User user = currentUser(authentication);
        SupportTicket ticket = new SupportTicket();
        ticket.setUser(user);
        ticket.setSubject(request.subject());
        ticket.setDescription(request.description());
        ticket.setRelatedOrder(resolveOrder(user.getId(), request.relatedOrderId()));
        ticket.setRelatedDelivery(resolveDelivery(user.getId(), request.relatedDeliveryId()));
        ticket.setRelatedServiceRequest(resolveServiceRequest(user.getId(), request.relatedServiceRequestId()));
        ticket = supportTicketRepository.save(ticket);
        auditService.record(user.getId(), "SUPPORT_TICKET_CREATED", "SUPPORT_TICKET", ticket.getId(), "{}");
        return toResponse(ticket);
    }

    @Transactional(readOnly = true)
    public List<SupportTicketResponse> listMine(Authentication authentication, int page, int size) {
        UUID userId = currentUser(authentication).getId();
        return supportTicketRepository.findByUser_IdOrderByCreatedAtDesc(userId, PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50))).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SupportTicketResponse getMine(Authentication authentication, UUID ticketId) {
        UUID userId = currentUser(authentication).getId();
        return toResponse(supportTicketRepository.findByIdAndUser_Id(ticketId, userId)
                .orElseThrow(() -> new BaseApplicationException("SUPPORT_TICKET_NOT_FOUND", "Support ticket was not found.")));
    }

    @Transactional(readOnly = true)
    public List<SupportTicketResponse> listAdmin(int page, int size) {
        return supportTicketRepository.findAll(PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100), Sort.by(Sort.Direction.DESC, "createdAt"))).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SupportTicketResponse getAdmin(UUID ticketId) {
        return toResponse(findTicket(ticketId));
    }

    @Transactional
    public SupportTicketResponse updateStatus(Authentication authentication, UUID ticketId, UpdateSupportTicketStatusRequest request) {
        User actor = currentUser(authentication);
        SupportTicket ticket = findTicket(ticketId);
        ticket.setStatus(request.status());
        if (request.adminNote() != null) {
            ticket.setAdminNote(request.adminNote());
        }
        ticket = supportTicketRepository.save(ticket);
        auditService.record(actor.getId(), "SUPPORT_TICKET_STATUS_UPDATED", "SUPPORT_TICKET", ticket.getId(), "{\"status\":\"" + request.status() + "\"}");
        notificationService.create(ticket.getUser().getId(), "Support ticket updated", "Your ticket '" + ticket.getSubject() + "' is now " + ticket.getStatus().name().replace('_', ' ') + ".");
        return toResponse(ticket);
    }

    public SupportTicketResponse toResponse(SupportTicket ticket) {
        return new SupportTicketResponse(
                ticket.getId(),
                ticket.getUser().getId(),
                ticket.getUser().getFullName(),
                ticket.getSubject(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getRelatedOrder() == null ? null : ticket.getRelatedOrder().getId(),
                ticket.getRelatedDelivery() == null ? null : ticket.getRelatedDelivery().getId(),
                ticket.getRelatedServiceRequest() == null ? null : ticket.getRelatedServiceRequest().getId(),
                ticket.getAdminNote(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }

    private SupportTicket findTicket(UUID ticketId) {
        return supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new BaseApplicationException("SUPPORT_TICKET_NOT_FOUND", "Support ticket was not found."));
    }

    private Order resolveOrder(UUID userId, UUID orderId) {
        if (orderId == null) {
            return null;
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BaseApplicationException("ORDER_NOT_FOUND", "Order was not found."));
        if (!canAccessOrder(userId, order)) {
            throw new BaseApplicationException("SUPPORT_ACCESS_DENIED", "You cannot open a ticket for this order.");
        }
        return order;
    }

    private Delivery resolveDelivery(UUID userId, UUID deliveryId) {
        if (deliveryId == null) {
            return null;
        }
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new BaseApplicationException("DELIVERY_NOT_FOUND", "Delivery was not found."));
        if (!canAccessDelivery(userId, delivery)) {
            throw new BaseApplicationException("SUPPORT_ACCESS_DENIED", "You cannot open a ticket for this delivery.");
        }
        return delivery;
    }

    private ServiceRequest resolveServiceRequest(UUID userId, UUID requestId) {
        if (requestId == null) {
            return null;
        }
        ServiceRequest serviceRequest = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new BaseApplicationException("SERVICE_REQUEST_NOT_FOUND", "Service request was not found."));
        if (!canAccessServiceRequest(userId, serviceRequest)) {
            throw new BaseApplicationException("SUPPORT_ACCESS_DENIED", "You cannot open a ticket for this service request.");
        }
        return serviceRequest;
    }

    private boolean canAccessOrder(UUID userId, Order order) {
        return order.getClient().getId().equals(userId)
                || authorizationService.canManageStore(userId, order.getStore().getId())
                || authorizationService.hasAnyRole(userId, List.of(PlatformRole.ADMIN, PlatformRole.SUPPORT_AGENT));
    }

    private boolean canAccessDelivery(UUID userId, Delivery delivery) {
        return canAccessOrder(userId, delivery.getOrder())
                || (delivery.getDriver() != null && delivery.getDriver().getUser().getId().equals(userId));
    }

    private boolean canAccessServiceRequest(UUID userId, ServiceRequest serviceRequest) {
        return serviceRequest.getClient().getId().equals(userId)
                || (serviceRequest.getServiceProvider() != null && authorizationService.canManageProvider(userId, serviceRequest.getServiceProvider().getId()))
                || authorizationService.hasAnyRole(userId, List.of(PlatformRole.ADMIN, PlatformRole.SUPPORT_AGENT));
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BaseApplicationException("UNAUTHENTICATED", "Authentication is required.");
        }
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new BaseApplicationException("USER_NOT_FOUND", "User was not found."));
    }
}

package com.lebanonplatform.modules.support.repository;

import com.lebanonplatform.modules.support.domain.SupportTicket;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, UUID> {

    Page<SupportTicket> findByUser_IdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Optional<SupportTicket> findByIdAndUser_Id(UUID id, UUID userId);
}

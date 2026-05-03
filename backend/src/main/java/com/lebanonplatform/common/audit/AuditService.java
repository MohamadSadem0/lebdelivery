package com.lebanonplatform.common.audit;

import com.lebanonplatform.modules.users.domain.User;
import com.lebanonplatform.modules.users.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditService(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(UUID actorUserId, String action, String entityType, UUID entityId, String metadataJson) {
        AuditLog auditLog = new AuditLog();
        auditLog.setActorUser(findActor(actorUserId).orElse(null));
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setMetadataJson(metadataJson);
        auditLogRepository.save(auditLog);
    }

    private Optional<User> findActor(UUID actorUserId) {
        if (actorUserId == null) {
            return Optional.empty();
        }
        return userRepository.findById(actorUserId);
    }
}

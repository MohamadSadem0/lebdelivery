package com.lebanonplatform.modules.clients.repository;

import com.lebanonplatform.modules.clients.domain.ClientProfile;
import com.lebanonplatform.modules.users.domain.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientProfileRepository extends JpaRepository<ClientProfile, UUID> {

    Optional<ClientProfile> findByUser(User user);
}

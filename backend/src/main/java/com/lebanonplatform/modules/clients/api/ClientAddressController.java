package com.lebanonplatform.modules.clients.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.clients.application.ClientAddressService;
import com.lebanonplatform.modules.clients.dto.request.CreateClientAddressRequest;
import com.lebanonplatform.modules.clients.dto.request.UpdateClientAddressRequest;
import com.lebanonplatform.modules.clients.dto.response.ClientAddressResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/client/addresses")
@PreAuthorize("hasRole('CLIENT')")
public class ClientAddressController {

    private final ClientAddressService clientAddressService;

    public ClientAddressController(ClientAddressService clientAddressService) {
        this.clientAddressService = clientAddressService;
    }

    @GetMapping
    public ApiResponse<List<ClientAddressResponse>> list(Authentication authentication) {
        return ApiResponse.success(clientAddressService.list(authentication));
    }

    @PostMapping
    public ApiResponse<ClientAddressResponse> create(Authentication authentication, @Valid @RequestBody CreateClientAddressRequest request) {
        return ApiResponse.success("Address saved.", clientAddressService.create(authentication, request));
    }

    @GetMapping("/{addressId}")
    public ApiResponse<ClientAddressResponse> get(Authentication authentication, @PathVariable UUID addressId) {
        return ApiResponse.success(clientAddressService.get(authentication, addressId));
    }

    @PatchMapping("/{addressId}")
    public ApiResponse<ClientAddressResponse> update(
            Authentication authentication,
            @PathVariable UUID addressId,
            @RequestBody UpdateClientAddressRequest request
    ) {
        return ApiResponse.success("Address updated.", clientAddressService.update(authentication, addressId, request));
    }

    @PostMapping("/{addressId}/default")
    public ApiResponse<ClientAddressResponse> setDefault(Authentication authentication, @PathVariable UUID addressId) {
        return ApiResponse.success("Default address updated.", clientAddressService.setDefault(authentication, addressId));
    }

    @DeleteMapping("/{addressId}")
    public ApiResponse<Void> delete(Authentication authentication, @PathVariable UUID addressId) {
        clientAddressService.delete(authentication, addressId);
        return ApiResponse.success("Address deleted.", null);
    }
}

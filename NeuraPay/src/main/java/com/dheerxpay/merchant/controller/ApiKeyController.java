package com.dheerxpay.merchant.controller;

import com.dheerxpay.common.api.ApiResponse;
import com.dheerxpay.merchant.dto.request.CreateApiKeyRequest;
import com.dheerxpay.merchant.dto.response.ApiKeyCreateResponse;
import com.dheerxpay.merchant.dto.response.ApiKeyResponse;
import com.dheerxpay.merchant.security.MerchantPrincipal;
import com.dheerxpay.merchant.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchants/{merchantId}/apikey")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    public ResponseEntity<ApiResponse<ApiKeyCreateResponse>> createApiKey(
            @PathVariable UUID merchantId,
            @RequestBody @Valid CreateApiKeyRequest apiKeyRequest
    ){
        MerchantPrincipal principal = MerchantPrincipal.current();
        assertMerchantAccess(principal, merchantId);
        ApiKeyCreateResponse apiKeyCreateResponse = apiKeyService.createApiKey(principal.merchantId(), apiKeyRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Merchant registered successfully",
                        apiKeyCreateResponse

                ));

    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ApiKeyResponse>>> getApiKeyListByMerchant(
            @PathVariable UUID merchantId
    ){
         MerchantPrincipal principal = MerchantPrincipal.current();
         assertMerchantAccess(principal, merchantId);
         List<ApiKeyResponse> apiKeyResponseList = apiKeyService.getApiKeyListByMerchant(principal.merchantId());

        return ResponseEntity
                .ok((ApiResponse.success(
                        "Merchant registered successfully",
                        apiKeyResponseList

                )));

    }

    @DeleteMapping("/{keyId}")
    public ResponseEntity<?> revoke(
            @PathVariable UUID merchantId,
            @PathVariable UUID keyId
    ){
        MerchantPrincipal principal = MerchantPrincipal.current();
        assertMerchantAccess(principal, merchantId);
        apiKeyService.revoke(principal.merchantId(), keyId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{keyId}/rotate")
    public ResponseEntity<ApiResponse<ApiKeyCreateResponse>> rotate(
            @PathVariable UUID merchantId,
            @PathVariable UUID keyId
    ){
        MerchantPrincipal principal = MerchantPrincipal.current();
        assertMerchantAccess(principal, merchantId);
        ApiKeyCreateResponse apiKeyCreateResponse = apiKeyService.rotate(principal.merchantId(), keyId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Merchant registered successfully",
                        apiKeyCreateResponse

                ));
    }
    private void assertMerchantAccess(MerchantPrincipal principal, UUID requestedMerchantId) {
        if (!principal.merchantId().equals(requestedMerchantId)) {
            throw new AccessDeniedException("Merchant access denied");
        }
    }
}

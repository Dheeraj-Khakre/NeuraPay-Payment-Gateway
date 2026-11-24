package com.dheerxpay.vault.controller;

import com.dheerxpay.common.api.ApiResponse;
import com.dheerxpay.merchant.security.MerchantPrincipal;
import com.dheerxpay.vault.dto.request.TokenizeRequest;
import com.dheerxpay.vault.dto.response.TokenizeResponse;
import com.dheerxpay.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/vault")
@RequiredArgsConstructor
public class VaultController {

    private final VaultService vaultService;

    @PostMapping("/tokenize")
    public ResponseEntity<ApiResponse<TokenizeResponse>> tokenizeCard(
            @RequestBody TokenizeRequest request
    ) {
        MerchantPrincipal principal = MerchantPrincipal.current();
        TokenizeResponse response = vaultService.tokenizeCard(request, principal.merchantId());
        return ResponseEntity.ok(ApiResponse.<TokenizeResponse>builder()
                        .message("Card tokenized successfully")
                        .timestamp(Instant.now())
                        .data(response)
                .build());
    }
}

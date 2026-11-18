package com.dheerxpay.vault.controller;

import com.dheerxpay.common.api.ApiResponse;
import com.dheerxpay.vault.dto.request.TokenizeRequest;
import com.dheerxpay.vault.dto.request.response.TokenizeResponse;
import com.dheerxpay.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vault")
@RequiredArgsConstructor
public class VaultController {

    private final VaultService vaultService;

    UUID merchantId = UUID.fromString("f3e1c8d0-4b2a-4c9e-9f5b-1a2b3c4d5e6f"); // Replace with your actual merchant ID



    @PostMapping("/tokenize")
    public ResponseEntity<ApiResponse<TokenizeResponse>> tokenizeCard(@RequestBody TokenizeRequest request) {
        // Implement the logic to tokenize the card and return the response

        TokenizeResponse response = vaultService.tokenizeCard(request, merchantId);
        return ResponseEntity.ok(ApiResponse.<TokenizeResponse>builder()
                        .message("Card tokenized successfully")
                        .timestamp(Instant.now())
                        .data(response)
                .build());
    }
}

package com.dheerxpay.vault.service;


import com.dheerxpay.vault.dto.request.TokenizeRequest;
import com.dheerxpay.vault.dto.response.TokenizeResponse;

import java.util.UUID;

public interface VaultService {
    TokenizeResponse tokenizeCard(TokenizeRequest request, UUID merchantId);
}

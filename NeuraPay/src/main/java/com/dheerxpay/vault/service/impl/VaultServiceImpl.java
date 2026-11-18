package com.dheerxpay.vault.service.impl;

import com.dheerxpay.common.enums.CardBrand;
import com.dheerxpay.common.utility.CardBrandUtil;
import com.dheerxpay.vault.dto.request.TokenizeRequest;
import com.dheerxpay.vault.dto.response.TokenizeResponse;
import com.dheerxpay.vault.repository.CardTokenRepository;
import com.dheerxpay.vault.repository.VaultCardRepository;
import com.dheerxpay.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VaultServiceImpl implements VaultService {

    private final CardTokenRepository cardTokenRepository;
    private final VaultCardRepository vaultCardRepository;

    @Override
    public TokenizeResponse tokenizeCard(TokenizeRequest request, UUID merchantId) {
        String lastFourDigits = request.pan().substring(request.pan().length() - 4);
        String bin = request.pan().substring(0, 6);
        CardBrand cardBrand = CardBrandUtil.getCardBrand(request.pan());
        return null;
    }
}

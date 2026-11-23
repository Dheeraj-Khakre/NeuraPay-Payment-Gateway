package com.dheerxpay.vault.service.impl;

import com.dheerxpay.common.entity.Money;
import com.dheerxpay.common.enums.CardBrand;
import com.dheerxpay.common.exceptions.ResourceNotFoundException;
import com.dheerxpay.common.utility.CardBrandUtil;
import com.dheerxpay.common.utility.RandomizerUtil;
import com.dheerxpay.payment.proccessor.PaymentProcessorRouter;
import com.dheerxpay.payment.proccessor.dto.PaymentProcessorRequest;
import com.dheerxpay.payment.proccessor.dto.PaymentProcessorResponse;
import com.dheerxpay.vault.config.VaultEncryptionConfig;
import com.dheerxpay.vault.dto.request.TokenizeRequest;
import com.dheerxpay.vault.dto.response.TokenizeResponse;
import com.dheerxpay.vault.entities.CardToken;
import com.dheerxpay.vault.entities.VaultCard;
import com.dheerxpay.vault.repository.CardTokenRepository;
import com.dheerxpay.vault.repository.VaultCardRepository;
import com.dheerxpay.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VaultServiceImpl implements VaultService {

    private final CardTokenRepository cardTokenRepository;
    private final VaultCardRepository vaultCardRepository;
    private final BytesEncryptor dekEncryptor;
    private final PaymentProcessorRouter paymentProcessorRouter;

    @Override
    public TokenizeResponse tokenizeCard(TokenizeRequest request, UUID merchantId) {
        String lastFourDigits = request.pan().substring(request.pan().length() - 4);
        String bin = request.pan().substring(0, 6);
        CardBrand cardBrand = CardBrandUtil.getCardBrand(request.pan());
        byte[]  dek = KeyGenerators.secureRandom(32).generateKey();

        byte[] encryptedPan= VaultEncryptionConfig.panEncryptor(dek).encrypt(
                request.pan().getBytes(StandardCharsets.UTF_8)
        );
        byte[] encryptedDek = dekEncryptor.encrypt(dek);

        VaultCard vaultCard =vaultCardRepository.save( VaultCard.builder()
                .encryptedPan(encryptedPan)
                .encryptedDek(encryptedDek)
                .lastFour(lastFourDigits)
                .cardBrand(cardBrand)
                .bin(bin)
                .cardHolderName(request.cardHolderName())
                .expiryMonth(request.expiryMonth())
                .expiryYear(request.expiryYear())
                .build());
        String token = "tok_"+ RandomizerUtil.randomBase64(32);

        CardToken cardToken = cardTokenRepository.save(
                CardToken.builder()
                        .token(token).
                        vaultCard(vaultCard)
                        .customer(request.customerId())
                        .merchant(merchantId).build()

        );

        return TokenizeResponse
                .builder()
                .token(token)
                .lastFour(lastFourDigits)
                .brand(cardBrand)
                .expiryMonth(vaultCard.getExpiryMonth())
                .expiryYear(vaultCard.getExpiryYear())
                .build();
    }

    @Override
    public PaymentProcessorResponse charge(UUID paymentId,String token, Money amount, Map<String, Object> methodDetails) {
        byte[] panByte = null;
        try {


            CardToken cardToken = cardTokenRepository.findByTokenAndRevokedAtIsNull(token)
                    .orElseThrow(() -> new ResourceNotFoundException("Card Token", token));
            VaultCard vaultCard = cardToken.getVaultCard();
            byte[] dek = dekEncryptor.decrypt(vaultCard.getEncryptedDek());
            panByte = VaultEncryptionConfig.panEncryptor(dek).decrypt(vaultCard.getEncryptedPan());
            String pan = new String(panByte, StandardCharsets.UTF_8);
            String expiry = vaultCard.getExpiryMonth() + "/" + vaultCard.getExpiryYear();

            PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest.card(paymentId, amount, methodDetails, pan, expiry);
            log.info("Vault Card charge register successfully  token {}****" ,token.substring(0,4));
            return paymentProcessorRouter.charge(
                    paymentProcessorRequest
            );

        } catch (RuntimeException e) {
           return PaymentProcessorResponse
                   .Failure
                   .builder()
                   .errorCode("VAULT_CARD_FAULURE")
                   .errorDescription(e.getMessage()).build();
        }finally {
            assert panByte != null;
            Arrays.fill(panByte, (byte) 0);
        }
    }
}

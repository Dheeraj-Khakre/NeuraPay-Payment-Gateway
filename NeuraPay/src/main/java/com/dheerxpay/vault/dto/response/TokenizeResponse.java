package com.dheerxpay.vault.dto.response;

import com.dheerxpay.common.enums.CardBrand;
import lombok.Builder;

@Builder
public record TokenizeResponse (
       String token,
       String lastFour,
       CardBrand brand,
       Integer expiryMonth,
       Integer expiryYear

){
}

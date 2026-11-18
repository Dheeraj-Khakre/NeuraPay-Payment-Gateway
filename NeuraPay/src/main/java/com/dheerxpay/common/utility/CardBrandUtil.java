package com.dheerxpay.common.utility;

import com.dheerxpay.common.enums.CardBrand;

public final class CardBrandUtil {

    private CardBrandUtil() {
    }

    public static CardBrand getCardBrand(String pan) {

        if (pan == null || pan.isBlank()) {
            return null;
        }

        String cardNumber = pan.replaceAll("\\s+", "");

        if (!cardNumber.matches("\\d{12,19}")) {
            return null;
        }

        // American Express: 34, 37
        if (cardNumber.matches("^3[47].*")) {
            return CardBrand.AMERICAN_EXPRESS;
        }

        // Visa: 4
        if (cardNumber.startsWith("4")) {
            return CardBrand.VISA;
        }

        // Mastercard: 51-55, 2221-2720
        if (cardNumber.matches("^(5[1-5]|2(2[2-9][0-9]|[3-6][0-9]{2}|7([01][0-9]|20))).*")) {
            return CardBrand.MASTERCARD;
        }

        // Discover
        if (cardNumber.matches("^(6011|65|64[4-9]|622).*")) {
            return CardBrand.DISCOVER;
        }

        // JCB: 3528-3589
        if (cardNumber.matches("^35(2[89]|[3-8][0-9]).*")) {
            return CardBrand.JCB;
        }

        // Diners Club
        if (cardNumber.matches("^(30[0-5]|3095|36|38|39).*")) {
            return CardBrand.DINERS_CLUB;
        }

        // Maestro (common ranges)
        if (cardNumber.matches("^(50|5[6-9]|6[0-9]).*")) {
            return CardBrand.MAESTRO;
        }

        // MIR
        if (cardNumber.matches("^220[0-4].*")) {
            return CardBrand.MIR;
        }

        // RuPay (common ranges)
        if (cardNumber.matches("^(508|6069|607|608|6521|6522).*")) {
            return CardBrand.RUPAY;
        }

        // Elo (common ranges; incomplete BIN coverage)
        if (cardNumber.matches("^(4011|4312|4389|4514|4576|5041|5066|5067|5090|6277|6362|6363).*")) {
            return CardBrand.ELO;
        }

        return null;
    }
}
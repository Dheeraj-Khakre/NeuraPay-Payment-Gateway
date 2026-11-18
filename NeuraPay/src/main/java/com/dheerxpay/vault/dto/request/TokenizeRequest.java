package com.dheerxpay.vault.dto.request;

import com.dheerxpay.vault.validation.FutureOrCurrentYear;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.LuhnCheck;
import org.hibernate.validator.constraints.UUID;

public record TokenizeRequest(
        @NotBlank(message = "PAN is required")
        @LuhnCheck(message = "PAN must be a valid card number")
        String pan,


        @NotBlank(message = "cvv is required")
        @Pattern(regexp = "^[0-9]{3,4}$", message = "cvv must contain 3 or 4 digits")
        String cvv,


        @NotNull(message = "Expiry month is required")
        @Min(value = 1, message = "Expiry month must be between 1 and 12")
        @Max(value = 12, message = "Expiry month must be between 1 and 12")
         Integer expiryMonth,

        @NotNull(message = "Expiry year is required")
        @Min(value = 2000, message = "Expiry year must be a four-digit year")
        @Max(value = 9999, message = "Expiry year must be a four-digit year")
        @FutureOrCurrentYear
         Integer expiryYear,

        @NotNull(message = "Customer ID is required")
        @UUID(message = "Customer ID must be a valid UUID")
         UUID customerId

) {

}

package com.dheerxpay.vault.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FutureOrCurrentYearValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureOrCurrentYear {

    String message() default "Expiry year must be the current year or a future year";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
package com.dheerxpay.vault.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class FutureOrCurrentYearValidator
        implements ConstraintValidator<FutureOrCurrentYear, Integer> {

    @Override
    public boolean isValid(Integer year, ConstraintValidatorContext context) {

        if (year == null) {
            return true;
        }

        int currentYear = Year.now().getValue();

        return year >= currentYear && year <= 9999;
    }
}
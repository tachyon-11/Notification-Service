package com.springboot.notificationsApp.validator;

import com.springboot.notificationsApp.dto.RangeDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, RangeDTO> {

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
    }

    @Override
    public boolean isValid(RangeDTO rangeDTO, ConstraintValidatorContext context) {
        if (rangeDTO.getStartDate() == null || rangeDTO.getEndDate() == null) {
            return true; // Not the responsibility of this validator
        }

        LocalDateTime startDate;
        LocalDateTime endDate;
        try {
            startDate = LocalDateTime.parse(rangeDTO.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            endDate = LocalDateTime.parse(rangeDTO.getEndDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return false;
        }

        return endDate.isAfter(startDate);
    }
}

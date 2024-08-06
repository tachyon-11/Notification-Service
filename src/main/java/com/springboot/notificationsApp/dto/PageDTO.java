package com.springboot.notificationsApp.dto;

import com.springboot.notificationsApp.validator.ValidDateRange;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PageDTO {
    @NotNull(message = "Page number cannot be null")
    @Min(value = 1, message = "Page number must be greater than 0")
    private Integer page;

    @NotNull(message = "Size cannot be null")
    @Min(value = 1, message = "Size must be at least 1")
    @Max(value = 10, message = "Size must be at most 10")
    private Integer size;
}

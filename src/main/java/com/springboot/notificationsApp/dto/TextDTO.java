package com.springboot.notificationsApp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TextDTO extends PageDTO{
    @NotBlank(message = "Please enter message")
    String message;
}

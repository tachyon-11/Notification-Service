package com.springboot.notificationsApp.dto;

import com.springboot.notificationsApp.validator.ValidPhoneNumber;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BlockDTO {
    @NotEmpty(message = "Phone number list cannot be empty")
    @Valid
    private List<@ValidPhoneNumber String> phoneNumbers;

    public BlockDTO() {
    }

    public BlockDTO(List<@ValidPhoneNumber String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

}

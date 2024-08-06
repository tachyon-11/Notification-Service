package com.springboot.notificationsApp.controller;

import com.springboot.notificationsApp.dto.BlockDTO;
import com.springboot.notificationsApp.entity.PhoneNumber;
import com.springboot.notificationsApp.exceptionHandling.NotificationException;
import com.springboot.notificationsApp.service.NotificationService;
import com.springboot.notificationsApp.validator.ValidPhoneNumber;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/blacklist")
public class BlacklistController {
    private final NotificationService smsService;

    public List<String> addCountryCode(List<String> phoneNumbers) {
        return phoneNumbers.stream()
                .map(phoneNumber -> "+91" + phoneNumber)
                .collect(Collectors.toList());
    }

    public BlacklistController(NotificationService smsService) {
        this.smsService = smsService;
    }

    @PostMapping
    public ResponseEntity<Object> addPhoneNumbersToBlacklist(@RequestBody @Valid BlockDTO request) {
        List<String> phoneNumbers = request.getPhoneNumbers();
        if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
            phoneNumbers = addCountryCode(phoneNumbers);
            smsService.addNumbers(phoneNumbers);
            return new ResponseEntity<>("Phone numbers added to blacklist successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid request", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getBlacklist() {
        List<PhoneNumber> blacklistedNumbers = smsService.queryForNumber();
        List<String> phoneNumbers = blacklistedNumbers.stream()
                .map(PhoneNumber::getPhoneNumber)
                .collect(Collectors.toList());
        BlockDTO response = new BlockDTO(phoneNumbers);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{request_id}")
    public PhoneNumber getBlacklistedNumber(@PathVariable @ValidPhoneNumber String request_id) {
        request_id = "+91" + request_id;
        return smsService.findByNumber(request_id);
    }

    @DeleteMapping
    public ResponseEntity<Object> deletePhoneNumbersFromBlacklist(@RequestBody @Valid BlockDTO request) {
        List<String> phoneNumbers = request.getPhoneNumbers();
        if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
            try {
                phoneNumbers = addCountryCode(phoneNumbers);
                smsService.deleteNumbers(phoneNumbers);
                return new ResponseEntity<>("Successfully whitelisted", HttpStatus.OK);
            } catch (Exception e) {
                throw new NotificationException("Failed to delete, Please Try again");
            }
        } else {
            throw new NotificationException("Invalid request: phone numbers list is empty or null");
        }
    }
}

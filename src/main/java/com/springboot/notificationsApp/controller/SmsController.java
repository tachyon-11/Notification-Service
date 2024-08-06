package com.springboot.notificationsApp.controller;

import com.springboot.notificationsApp.dto.SmsDTO;
import com.springboot.notificationsApp.entity.SMS;
import com.springboot.notificationsApp.response.SmsResponse;
import com.springboot.notificationsApp.service.NotificationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/sms/")
public class SmsController {
    private final NotificationService smsService;

    public SmsController(NotificationService smsService) {
        this.smsService = smsService;
    }

    @GetMapping("/allMessage")
    public ResponseEntity<Object> getAllMessage(){
        return new ResponseEntity<>( smsService.queryForMessage(), HttpStatus.OK);
    }

    @GetMapping("/{request_id}")
    public ResponseEntity<Object> getMessage(@PathVariable int request_id) {
        SMS theMessage = smsService.findById(request_id);
        return new ResponseEntity<>(new SmsResponse(theMessage.getMessage(), theMessage.getId().toString(), LocalDateTime.now()), HttpStatus.OK);
    }

    @PostMapping("/send")
    public ResponseEntity<Object> sendMessage(@Valid @NotNull @RequestBody SmsDTO newSmsDTO){
        return new ResponseEntity<>(smsService.save(newSmsDTO), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{request_id}")
    public ResponseEntity<Object> deleteMessage(@PathVariable int request_id) {
        smsService.deleteSMS(request_id);
        return new ResponseEntity<>("Message with id :- " + request_id + " has been deleted successfully",HttpStatus.OK);
    }

}

//Exception handling
//Logging
//Testing


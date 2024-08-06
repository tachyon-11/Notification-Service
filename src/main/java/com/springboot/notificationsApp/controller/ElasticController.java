package com.springboot.notificationsApp.controller;

import com.springboot.notificationsApp.dto.RangeDTO;
import com.springboot.notificationsApp.dto.TextDTO;
import com.springboot.notificationsApp.elasticsearch.model.MessageModel;
import com.springboot.notificationsApp.elasticsearch.queryService.ElasticSearchQuery;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/v1/elastic")
public class ElasticController {
    private final ElasticSearchQuery elasticSearchQuery;

    public ElasticController(ElasticSearchQuery elasticSearchQuery) {
        this.elasticSearchQuery = elasticSearchQuery;
    }

    @PostMapping("/createOrUpdateMessage")
    public ResponseEntity<Object> createOrUpdateMessage(@RequestBody MessageModel sentMessage) throws IOException {
        String response = elasticSearchQuery.createOrUpdateMessage(sentMessage);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getMessage/{requestId}")
    public ResponseEntity<Object> getMessageById(@PathVariable String requestId) throws IOException {
        MessageModel message =  elasticSearchQuery.getMessageById(requestId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @DeleteMapping("/deleteMessage/{requestId}")
    public ResponseEntity<Object> deleteMessageById(@PathVariable String requestId) throws IOException {
        String response =  elasticSearchQuery.deleteMessageById(requestId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/range")
    public ResponseEntity<Object> getMessagesByCreatedAtRange(@Valid @NotNull @RequestBody RangeDTO rangeDTO) throws IOException {
        return new ResponseEntity<>(elasticSearchQuery.getMessagesByCreatedAtRange(rangeDTO.getStartDateTime(), rangeDTO.getEndDateTime(), rangeDTO.getPage(), rangeDTO.getSize()), HttpStatus.OK);
    }

    @GetMapping("/searchMessage")
    public ResponseEntity<Object> searchMessagesByText(@Valid @NotNull @RequestBody TextDTO textDTO) throws IOException {
        return new ResponseEntity<>(elasticSearchQuery.searchMessagesByText(textDTO.getMessage(), textDTO.getPage(), textDTO.getSize()), HttpStatus.OK);
    }
}

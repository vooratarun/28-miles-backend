package com.quodex._miles.controller;

import com.quodex._miles.io.MongoInsertRequest;
import com.quodex._miles.io.MongoInsertResponse;
import com.quodex._miles.service.MongoDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mongodb")
@RequiredArgsConstructor
public class MongoController {

    private final MongoDocumentService mongoDocumentService;

    @PostMapping("/{collectionName}")
    public ResponseEntity<MongoInsertResponse> insertDocument(
            @PathVariable String collectionName,
            @RequestBody MongoInsertRequest request
    ) {
        MongoInsertResponse response = mongoDocumentService.insertDocument(collectionName, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

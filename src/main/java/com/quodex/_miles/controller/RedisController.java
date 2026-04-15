package com.quodex._miles.controller;

import com.quodex._miles.io.RedisInsertRequest;
import com.quodex._miles.io.RedisInsertResponse;
import com.quodex._miles.io.RedisValueResponse;
import com.quodex._miles.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisController {

    private final RedisService redisService;

    @PostMapping("/insert")
    public ResponseEntity<RedisInsertResponse> insert(@RequestBody RedisInsertRequest request) {
        RedisInsertResponse response = redisService.insert(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{key}")
    public ResponseEntity<RedisValueResponse> getByKey(@PathVariable String key) {
        return ResponseEntity.ok(redisService.getByKey(key));
    }
}

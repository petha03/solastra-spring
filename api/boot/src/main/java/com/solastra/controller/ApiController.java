package com.solastra.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/test")
public class ApiController {

    @PostMapping
    public ResponseEntity<String> test(@RequestBody String body) {
        String response = "Echo: " + body;
        return ResponseEntity.ok(response);
    }
}
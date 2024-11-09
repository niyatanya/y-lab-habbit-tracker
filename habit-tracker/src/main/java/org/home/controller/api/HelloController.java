package org.home.controller.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.home.logging.annotations.LoggableUserAction;

import java.util.Map;

@RestController
@RequestMapping("/hello")
@LoggableUserAction
public class HelloController {
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> hello() {
        System.out.println("Hurrey! You did it! Spring Boot Web App is working!");
        Map<String, String> result = Map.of("status", "OK",
                "message", "You did it!");
        return ResponseEntity.ok(result);
    }
}

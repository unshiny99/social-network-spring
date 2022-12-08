package com.bd.socialnetwork;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/health")
public class HealthController {
    @GetMapping(path = "mongo")
    public ResponseEntity<String> getMongo() {
        return ResponseEntity.ok("Connexion to DB suceeded");
    }
}

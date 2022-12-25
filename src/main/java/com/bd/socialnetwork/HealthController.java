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
        // TODO : change this check with a real DB connection test
        return ResponseEntity.ok("Connexion to DB suceeded");
    }

    @GetMapping(path = "spring-server")
    public ResponseEntity<String> getServer() {
        return ResponseEntity.ok("Connexion to DB suceeded");
    }
}

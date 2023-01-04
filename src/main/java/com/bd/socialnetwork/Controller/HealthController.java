package com.bd.socialnetwork.Controller;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/health")
public class HealthController {
    @GetMapping(path = "mongo")
    public ResponseEntity<String> getMongo() {
        // TODO : Replace the uri string with your MongoDB deployment's connection string
        // add env vars to not do errors...
        String uri = "mongodb://10.200.0.5:27016";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("admin");
            try {
                Bson command = new BsonDocument("ping", new BsonInt64(1));
                Document commandResult = database.runCommand(command);
                System.out.println("Connected successfully to server.");
                return ResponseEntity.ok("Connexion to DB suceeded");
            } catch (MongoException me) {
                System.err.println("An error occurred while attempting to connect : " + me);
                return ResponseEntity.ok("Unable to connect to DB :/");
            }
        }
    }

    @GetMapping(path = "spring-server")
    public ResponseEntity<String> getServer() {
        return ResponseEntity.ok("Connexion to Spring server suceeded");
    }
}

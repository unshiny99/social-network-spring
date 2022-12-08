package com.bd.socialnetwork;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "")
public class UserController {
    @Autowired
    private final MongoTemplate mongoTemplate;

    @Autowired
    public UserController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @RequestMapping("")
    public String home() {
        return "index";
    }

    @GetMapping("getAllUsers")
    public List<UserEntity> getAllUsers() {
        return mongoTemplate.findAll(UserEntity.class);
    }
}

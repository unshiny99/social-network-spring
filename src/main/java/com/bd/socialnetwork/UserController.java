package com.bd.socialnetwork;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "")
public class UserController {
    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;

    @Autowired
    public UserController(MongoTemplate mongoTemplate, UserRepository userRepository) {
        this.mongoTemplate = mongoTemplate;
        this.userRepository = userRepository;
    }

    @PostMapping("/addUser")
    public UserEntity addUser(@RequestBody UserEntity user) {
        if (userRepository.existsUserEntityByLogin(user.getLogin())) {
            throw new ExistingLoginException("Le login existe déjà");
        }
        return userRepository.save(user);
    }

    @GetMapping("getAllUsers")
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }
}

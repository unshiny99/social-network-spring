package com.bd.socialnetwork;

import com.bd.socialnetwork.Exception.ExistingLoginException;
import com.bd.socialnetwork.Exception.NotFoundLoginException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
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

    @PostMapping("addUser")
    public UserEntity addUser(@RequestBody UserEntity user) {
        if (userRepository.existsUserEntityByLogin(user.getLogin())) {
            throw new ExistingLoginException("Le login existe déjà");
        }
        return userRepository.save(user);
    }

    @GetMapping("getUser")
    public UserEntity getUser(@RequestParam("login") String login) {
        if (!userRepository.existsUserEntityByLogin(login)) {
            throw new NotFoundLoginException("Le login n'a pas été trouvé");
        }
        return userRepository.findByLogin(login);
    }

    @GetMapping("getAllUsers")
    public List<UserEntity> getAllUsers(@RequestParam("pageNumber") int pageNumber) {
        Query query = new Query();
        int pageSize = 30;
        query.skip((long) (pageNumber-1) * pageSize);
        query.limit(pageSize);
        return mongoTemplate.find(query, UserEntity.class);
    }
}

package com.bd.socialnetwork;

import com.bd.socialnetwork.Exception.ExistingException;
import com.bd.socialnetwork.Exception.NotFoundLoginException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            throw new ExistingException("Le login existe déjà");
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

    @PatchMapping("addFriend")
    public ResponseEntity addFriend(@RequestParam("loginUser1") String loginUser1, @RequestParam("loginUser2") String loginUser2) {
//        if (userRepository.existsUserEntityByLogin(user.getLogin())) {
//            throw new ExistingException("La relation d'amitié est déjà existante");
//        }
//        return userRepository.save(user);
        UserEntity user1 = userRepository.findByLogin(loginUser1);
        UserEntity user2 = userRepository.findByLogin(loginUser2);
        if (user1.getFriends().contains(loginUser2) && user2.getFriends().contains(loginUser1)) {
            throw new ExistingException("La relation d'amitié existe déjà.");
        } else if (user1.getFriends().contains(loginUser2) && !user2.getFriends().contains(loginUser1) ||
                !user1.getFriends().contains(loginUser2) && user2.getFriends().contains(loginUser1)) {
            throw new ExistingException("La relation d'amitié existe déjà dans un sens.");
        }
        // all this should be a transaction
        List<String> friendsUser1 = user1.getFriends();
        friendsUser1.add(loginUser2);
        user1.setFriends(friendsUser1);
        userRepository.save(user1);

        List<String> friendsUser2 = user2.getFriends();
        friendsUser2.add(loginUser1);
        user2.setFriends(friendsUser2);
        userRepository.save(user2);

        return ResponseEntity.status(HttpStatus.OK).body("Relation d'amitié ajoutée avec succès");
    }
}

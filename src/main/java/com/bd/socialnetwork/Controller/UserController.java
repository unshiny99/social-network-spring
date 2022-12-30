package com.bd.socialnetwork.Controller;

import com.bd.socialnetwork.Entity.UserEntity;
import com.bd.socialnetwork.Exception.ExistingException;
import com.bd.socialnetwork.Exception.NotFoundException;
import com.bd.socialnetwork.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

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
        if (userRepository.existsUserEntityByLoginIgnoreCase(user.getLogin())) {
            throw new ExistingException("Le login existe déjà");
        }
        return userRepository.save(user);
    }

    @GetMapping("getUser")
    public UserEntity getUser(@RequestParam("login") String login) {
        if (!userRepository.existsUserEntityByLoginIgnoreCase(login)) {
            throw new NotFoundException("Le login n'a pas été trouvé");
        }
        return userRepository.findByLoginIgnoreCase(login);
    }

    @GetMapping("getAllUsers")
    public List<UserEntity> getAllUsers(@RequestParam("pageNumber") int pageNumber) {
        initFriends();
        Query query = new Query();
        int pageSize = 30;
        query.skip((long) (pageNumber-1) * pageSize);
        query.limit(pageSize);
        return mongoTemplate.find(query, UserEntity.class);
    }

    /**
     * generate random 3 to 17 friends relationship for each user
     * checks if the relation doesn't exist yet and if it is not current user
     */
    public void initFriends() {
        Random random = new Random();
        for (UserEntity user : userRepository.findAll()) {
            // Generates random integers 0 to 20
            int numberFriends = random.nextInt(17)+3;
            List<String> friends = new ArrayList<>();
            for (int i=0; i<numberFriends; i++) {
                List<UserEntity> allUsers = userRepository.findAll();
                int numberUser = random.nextInt(allUsers.size());
                UserEntity userToAdd = allUsers.get(numberUser);
                if (!friends.contains(userToAdd.getId()) && !Objects.equals(userToAdd.getId(), user.getId())) {
                    friends.add(userToAdd.getId());
                } else {
                    i--;
                }
            }
            user.setFriends(friends);
            userRepository.save(user);
        }
    }

    @PatchMapping("addFriend")
    public ResponseEntity<String> addFriend(@RequestParam("loginUser1") String loginUser1, @RequestParam("loginUser2") String loginUser2) {
        UserEntity user1 = userRepository.findByLoginIgnoreCase(loginUser1);
        UserEntity user2 = userRepository.findByLoginIgnoreCase(loginUser2);
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

    @GetMapping("getFriends")
    public List<UserEntity> getFriends(@RequestParam String loginUser) {
        if (!userRepository.existsUserEntityByLoginIgnoreCase(loginUser)) {
            throw new NotFoundException("Le login n'a pas été trouvé");
        } else {
            UserEntity user = userRepository.findByLoginIgnoreCase(loginUser);
            List<String> friends = user.getFriends();

            List<UserEntity> userFriends = new ArrayList<>();
            for(String friend : friends) {
                UserEntity userFriend = userRepository.findByLoginIgnoreCase(friend);
                userFriends.add(userFriend);
            }
            return userFriends;
        }
    }

    @PatchMapping("removeFriend")
    public ResponseEntity<String> removeFriend(@RequestParam("loginUser1") String loginUser1, @RequestParam("loginUser2") String loginUser2) {
        UserEntity user1 = userRepository.findByLoginIgnoreCase(loginUser1);
        UserEntity user2 = userRepository.findByLoginIgnoreCase(loginUser2);
        if (!user1.getFriends().contains(loginUser2) && !user2.getFriends().contains(loginUser1)) {
            throw new ExistingException("La relation d'amitié n'existe pas.");
        } else if (user1.getFriends().contains(loginUser2) && !user2.getFriends().contains(loginUser1)) {
            //throw new ExistingException("La relation d'amitié existe dans un sens.");
            List<String> friendsUser1 = user1.getFriends();
            friendsUser1.remove(loginUser2);
            user1.setFriends(friendsUser1);
            userRepository.save(user1);
        } else if(!user1.getFriends().contains(loginUser2) && user2.getFriends().contains(loginUser1)) {
            List<String> friendsUser2 = user2.getFriends();
            friendsUser2.remove(loginUser1);
            user2.setFriends(friendsUser2);
            userRepository.save(user2);
        } else {
            // all this should be a transaction
            List<String> friendsUser1 = user1.getFriends();
            friendsUser1.remove(loginUser2);
            user1.setFriends(friendsUser1);
            userRepository.save(user1);

            List<String> friendsUser2 = user2.getFriends();
            friendsUser2.remove(loginUser1);
            user2.setFriends(friendsUser2);
            userRepository.save(user2);
        }
        return ResponseEntity.status(HttpStatus.OK).body("Relation d'amitié supprimée avec succès");
    }

    @GetMapping("getUsersByDescription")
    public List<UserEntity> getUsersByDescription(@RequestParam String description) {
        return userRepository.findByDescriptionLikeIgnoreCase(description);
    }
}

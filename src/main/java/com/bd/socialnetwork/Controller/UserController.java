package com.bd.socialnetwork.Controller;

import com.bd.socialnetwork.Entity.PostEntity;
import com.bd.socialnetwork.Entity.UserEntity;
import com.bd.socialnetwork.Exception.ExistingException;
import com.bd.socialnetwork.Exception.NotFoundException;
import com.bd.socialnetwork.Repository.PostRepository;
import com.bd.socialnetwork.Repository.UserRepository;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

@RestController
@RequestMapping(path = "")
public class UserController {
    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    @Value("#{environment['spring.security.user.name']}")
    private String username;
    @Value("#{environment['spring.security.user.password']}")
    private String password;
    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    public UserController(MongoTemplate mongoTemplate, UserRepository userRepository, PostRepository postRepository) {
        this.mongoTemplate = mongoTemplate;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @PostMapping("addUser")
    public ResponseEntity<String> addUser(@RequestBody UserEntity user) {
        if (userRepository.existsUserEntityByLoginIgnoreCase(user.getLogin())) {
            throw new ExistingException("Le login existe déjà");
        }
        userRepository.save(user);
        logger.log(Level.getLevel("DIAG"), "Un utilisateur a été ajouté : {}", user.getId());
        return ResponseEntity.status(HttpStatus.OK).body("Utilisateur ajouté avec succès");
    }

    @GetMapping("getUser")
    public UserEntity getUser(@RequestParam("login") String login) {
        // feature : create a postMap & patchMap to load data and init friends ?
        // comment the lines below if data exists in DB
//        loadData();
//        initFriends();
        if (!userRepository.existsUserEntityByLoginIgnoreCase(login)) {
            throw new NotFoundException("Le login n'a pas été trouvé");
        }
        logger.log(Level.getLevel("DIAG"), "Un utilisateur a été recherché : {}", userRepository.findByLoginIgnoreCase(login).getId());
        return userRepository.findByLoginIgnoreCase(login);
    }

    @GetMapping("getAllUsers")
    public List<UserEntity> getAllUsers(@RequestParam("pageNumber") int pageNumber) {
        Query query = new Query();
        int pageSize = 30;
        query.skip((long) (pageNumber-1) * pageSize);
        query.limit(pageSize);
        return mongoTemplate.find(query, UserEntity.class);
    }

    /**
     * load initial user data by inserting users from given JSON file
     */
    public void loadData() {
        JSONParser jsonParser = new JSONParser();
        try {
            String jsonPath = "src/main/resources/data/userData.json";
            Object object = jsonParser.parse(new FileReader(jsonPath));

            //convert Object to JSONObject
            JSONObject jsonObject = (JSONObject) object;
            //System.out.println(jsonObject.get("ctRoot"));
            JSONArray listUsers = (JSONArray) jsonObject.get("ctRoot");
            for (Object user : listUsers) {
                JSONObject userObject = (JSONObject) user;
                HttpPost post = new HttpPost("http://localhost:80/addUser");
                StringEntity params = new StringEntity(userObject.toString());
                post.addHeader("content-type", "application/json");

                String valueToEncode = username + ":" + password;
                String token = "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
                post.addHeader("Authorization",token);
                post.setEntity(params);
                try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    httpClient.execute(post);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (user1 == null || user2 == null) {
            throw new NotFoundException("Un des utilisateurs n'a pas été trouvé.");
        }
        if (user1.getFriends().contains(user2.getId()) && user2.getFriends().contains(user1.getId())) {
            throw new ExistingException("La relation d'amitié existe déjà.");
        } else if (user1.getFriends().contains(user2.getId()) && !user2.getFriends().contains(user1.getId()) ||
                !user1.getFriends().contains(user2.getId()) && user2.getFriends().contains(user1.getId())) {
            throw new ExistingException("La relation d'amitié existe déjà dans un sens.");
        }
        // all this should be a transaction
        List<String> friendsUser1 = user1.getFriends();
        friendsUser1.add(user2.getId());
        user1.setFriends(friendsUser1);
        userRepository.save(user1);

        List<String> friendsUser2 = user2.getFriends();
        friendsUser2.add(user1.getId());
        user2.setFriends(friendsUser2);
        userRepository.save(user2);

        logger.log(Level.getLevel("DIAG"), "Une relation d'amitié a été ajoutée, entre {} et {}", user1.getId(), user2.getId());
        return ResponseEntity.status(HttpStatus.OK).body("Relation d'amitié ajoutée avec succès");
    }

    @GetMapping("getFriends")
    public List<Optional<UserEntity>> getFriends(@RequestParam String loginUser) {
        if (!userRepository.existsUserEntityByLoginIgnoreCase(loginUser)) {
            throw new NotFoundException("Le login n'a pas été trouvé");
        } else {
            UserEntity user = userRepository.findByLoginIgnoreCase(loginUser);
            List<String> friends = user.getFriends();

            List<Optional<UserEntity>> userFriends = new ArrayList<>();
            for(String friend : friends) {
                UserEntity userFriend = userRepository.findById(friend);
                userFriends.add(Optional.ofNullable(userFriend));
            }

            logger.log(Level.getLevel("DIAG"), "Un accès aux amis a été réalisé : {}", user.getId());
            return userFriends;
        }
    }

    @PatchMapping("removeFriend")
    public ResponseEntity<String> removeFriend(@RequestParam("loginUser1") String loginUser1, @RequestParam("loginUser2") String loginUser2) {
        UserEntity user1 = userRepository.findByLoginIgnoreCase(loginUser1);
        UserEntity user2 = userRepository.findByLoginIgnoreCase(loginUser2);
        if (user1 == null || user2 == null) {
            throw new NotFoundException("Un des utilisateurs n'a pas été trouvé.");
        }
        if (!user1.getFriends().contains(user2.getId()) && !user2.getFriends().contains(user1.getId())) {
            throw new ExistingException("La relation d'amitié n'existe pas.");
        } else if (user1.getFriends().contains(user2.getId()) && !user2.getFriends().contains(user1.getId())) {
            //throw new ExistingException("La relation d'amitié existe dans un sens.");
            List<String> friendsUser1 = user1.getFriends();
            friendsUser1.remove(loginUser2);
            user1.setFriends(friendsUser1);
            userRepository.save(user1);
        } else if(!user1.getFriends().contains(user2.getId()) && user2.getFriends().contains(user1.getId())) {
            List<String> friendsUser2 = user2.getFriends();
            friendsUser2.remove(loginUser1);
            user2.setFriends(friendsUser2);
            userRepository.save(user2);
        } else {
            // all this should be a transaction
            List<String> friendsUser1 = user1.getFriends();
            friendsUser1.remove(user2.getId());
            user1.setFriends(friendsUser1);
            userRepository.save(user1);

            List<String> friendsUser2 = user2.getFriends();
            friendsUser2.remove(user1.getId());
            user2.setFriends(friendsUser2);
            userRepository.save(user2);
        }
        logger.log(Level.getLevel("DIAG"), "Une relation d'amitié a été supprimée, entre {} et {}", user1.getId(), user2.getId());
        return ResponseEntity.status(HttpStatus.OK).body("Relation d'amitié supprimée avec succès");
    }

    @GetMapping("getUsersByDescription")
    public List<UserEntity> getUsersByDescription(@RequestParam String description) {
        return userRepository.findByDescriptionLikeIgnoreCase(description);
    }

    @GetMapping("getActu")
    public List<PostEntity> getActu(@RequestParam String loginUser) {
        List<PostEntity> posts = new ArrayList<>();
        if (!userRepository.existsUserEntityByLoginIgnoreCase(loginUser)) {
            throw new NotFoundException("Le login n'a pas été trouvé");
        } else {
            UserEntity user = userRepository.findByLoginIgnoreCase(loginUser);
            List<String> friends = user.getFriends();

            for(String friend : friends) {
                UserEntity userFriend = userRepository.findById(friend);
                posts.addAll(postRepository.findAllByUser(userFriend.getId()));
            }
            logger.log(Level.getLevel("DIAG"), "Une demande d'actualité a été faite pour : {}", user.getId());
        }
        // sort the list by date DESC
        posts.sort(new Comparator<PostEntity>() {
            public int compare(PostEntity m1, PostEntity m2) {
                return m2.getDateTime().compareTo(m1.getDateTime());
            }
        });
        return posts;
    }

    @GetMapping("getLogsUser")
    public List<String> getLogsUser(@RequestParam("loginUser") String loginUser) {
        List<String> logs = new ArrayList<>();
        if (!userRepository.existsUserEntityByLoginIgnoreCase(loginUser)) {
            throw new NotFoundException("Le login n'a pas été trouvé");
        } else {
            // first, get id of user
            UserEntity user = userRepository.findByLoginIgnoreCase(loginUser);
            String idUser = user.getId();
            File file = new File("logs/test.log");
            Scanner sc = null;
            try {
                sc = new Scanner(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            while (sc.hasNextLine()) {
                //System.out.println(sc.nextLine());
                String logLine = sc.nextLine();
                if(logLine.contains(idUser)) {
                    logs.add(logLine);
                }
            }
        }
        return logs;
    }
}

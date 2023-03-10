package com.bd.socialnetwork.Controller;

import com.bd.socialnetwork.Entity.PostEntity;
import com.bd.socialnetwork.Exception.InvalidParameterException;
import com.bd.socialnetwork.Exception.NotFoundException;
import com.bd.socialnetwork.Repository.PostRepository;
import com.bd.socialnetwork.Repository.UserRepository;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class PostController {
    private final MongoTemplate mongoTemplate;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LogManager.getLogger(PostController.class);

    public PostController(MongoTemplate mongoTemplate, PostRepository postRepository, UserRepository userRepository) {
        this.mongoTemplate = mongoTemplate;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("addPost")
    public ResponseEntity<String> addPost(@RequestParam String content, @RequestParam String loginUser) {
        if(!userRepository.existsUserEntityByLoginIgnoreCase(loginUser)) {
            throw new NotFoundException("Le login n'a pas été trouvé");
        }
        if (content == null || content.isEmpty()) {
            throw new InvalidParameterException("Le post doit posséder du contenu");
        }
        // update the login to id of user
        String idUser = userRepository.findByLoginIgnoreCase(loginUser).getId();
        PostEntity post = new PostEntity(content, idUser, LocalDateTime.now());
        postRepository.save(post);
        logger.log(Level.getLevel("DIAG"), "Un post a été ajouté : {}", idUser);
        return ResponseEntity.status(HttpStatus.OK).body("Post ajouté avec succès");
    }

    @GetMapping("getAllPost")
    public List<PostEntity> getAllPost(@RequestParam String loginUser) {
        if(!userRepository.existsUserEntityByLoginIgnoreCase(loginUser)) {
            throw new NotFoundException("Le login n'a pas été trouvé");
        }
        String idUser = userRepository.findByLoginIgnoreCase(loginUser).getId();
        logger.log(Level.getLevel("DIAG"), "Liste des posts demandée : {}", idUser);
        return postRepository.findAllByUser(idUser);
    }
}

package com.bd.socialnetwork.Controller;

import com.bd.socialnetwork.Entity.TchatEntity;
import com.bd.socialnetwork.Exception.ExistingException;
import com.bd.socialnetwork.Exception.NotFoundException;
import com.bd.socialnetwork.Repository.TchatRepository;
import com.bd.socialnetwork.Repository.UserRepository;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TchatController {
    private final MongoTemplate mongoTemplate;
    private final TchatRepository tchatRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LogManager.getLogger(TchatController.class);

    @Autowired
    public TchatController(MongoTemplate mongoTemplate, TchatRepository tchatRepository, UserRepository userRepository) {
        this.mongoTemplate = mongoTemplate;
        this.tchatRepository = tchatRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("addTchat")
    public TchatEntity addTchat(@RequestParam("loginUser1") String loginUser1, @RequestParam("loginUser2") String loginUser2) {
        String idUser1 = userRepository.findByLoginIgnoreCase(loginUser1).getId();
        String idUser2 = userRepository.findByLoginIgnoreCase(loginUser2).getId();
        if(idUser1 == null ||idUser2 == null) {
            throw new NotFoundException("Au moins un des deux utilisateurs n'a pas été trouvé");
        }
        if ((tchatRepository.existsUserEntityByUser1(idUser1) && tchatRepository.existsUserEntityByUser2(idUser2))
                || (tchatRepository.existsUserEntityByUser1(idUser2) && tchatRepository.existsUserEntityByUser2(idUser1))) {
            throw new ExistingException("Un tchat existe déjà entre ces 2 personnes");
        }
        TchatEntity tchatEntity = new TchatEntity(idUser1, idUser2);
        logger.log(Level.getLevel("DIAG"), "Tchat initialisé entre {} et {}", idUser1, idUser2);
        return tchatRepository.save(tchatEntity);
    }

    @GetMapping("getTchat")
    public TchatEntity getTchat(@RequestParam("loginUser1") String loginUser1, @RequestParam("loginUser2") String loginUser2) {
        String idUser1 = userRepository.findByLoginIgnoreCase(loginUser1).getId();
        String idUser2 = userRepository.findByLoginIgnoreCase(loginUser2).getId();
        if(idUser1 == null ||idUser2 == null) {
            throw new NotFoundException("Au moins un des deux utilisateurs n'a pas été trouvé");
        }
        if(tchatRepository.findByUser1AndUser2(idUser1, idUser2) == null) {
            if (tchatRepository.findByUser1AndUser2(idUser2, idUser1) == null) {
                throw new NotFoundException("Le tchat n'a pas été trouvé entre ces 2 personnes");
            } else { // tchat was found in other side
                return tchatRepository.findByUser1AndUser2(idUser2, idUser1);
            }
        }
        logger.log(Level.getLevel("DIAG"), "Tchat récupéré entre {} et {}", idUser1, idUser2);
        return tchatRepository.findByUser1AndUser2(idUser1, idUser2);
    }
}

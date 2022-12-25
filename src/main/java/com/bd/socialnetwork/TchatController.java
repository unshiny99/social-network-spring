package com.bd.socialnetwork;

import com.bd.socialnetwork.Entity.TchatEntity;
import com.bd.socialnetwork.Entity.UserEntity;
import com.bd.socialnetwork.Exception.ExistingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TchatController {
    private final MongoTemplate mongoTemplate;
    private final TchatRepository tchatRepository;
    private final UserRepository userRepository;

    @Autowired
    public TchatController(MongoTemplate mongoTemplate, TchatRepository tchatRepository, UserRepository userRepository) {
        this.mongoTemplate = mongoTemplate;
        this.tchatRepository = tchatRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("addTchat")
    public TchatEntity addTchat(@RequestParam("loginUser1") String loginUser1, @RequestParam("loginUser2") String loginUser2) {
        String idUser1 = userRepository.findByLogin(loginUser1).getId();
        String idUser2 = userRepository.findByLogin(loginUser2).getId();
        if ((tchatRepository.existsUserEntityByUser1(idUser1) && tchatRepository.existsUserEntityByUser2(idUser2))
                || (tchatRepository.existsUserEntityByUser1(idUser2) && tchatRepository.existsUserEntityByUser2(idUser1))) {
            throw new ExistingException("Un tchat existe déjà entre ces 2 personnes");
        }
        TchatEntity tchatEntity = new TchatEntity(idUser1, idUser2);
        return tchatRepository.save(tchatEntity);
    }
}

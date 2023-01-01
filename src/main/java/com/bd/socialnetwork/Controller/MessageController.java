package com.bd.socialnetwork.Controller;

import com.bd.socialnetwork.Entity.MessageEntity;
import com.bd.socialnetwork.Entity.TchatEntity;
import com.bd.socialnetwork.Exception.NotFoundException;
import com.bd.socialnetwork.Repository.MessageRepository;
import com.bd.socialnetwork.Repository.TchatRepository;
import com.bd.socialnetwork.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class MessageController {
    private final MongoTemplate mongoTemplate;
    private final MessageRepository messageRepository;
    private final TchatRepository tchatRepository;
    private final UserRepository userRepository;

    @Autowired
    public MessageController(MongoTemplate mongoTemplate, MessageRepository messageRepository, TchatRepository tchatRepository, UserRepository userRepository) {
        this.mongoTemplate = mongoTemplate;
        this.messageRepository = messageRepository;
        this.tchatRepository = tchatRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("addMessage")
    public ResponseEntity<String> addMessage(@RequestBody MessageEntity message) {
        TchatEntity tchatEntity;
        if(tchatRepository.findByUser1AndUser2(message.getSender(), message.getRecipient()) == null) {
            if (tchatRepository.findByUser1AndUser2(message.getRecipient(), message.getSender()) == null) {
                throw new NotFoundException("Impossible de créer le message. Le tchat n'a pas été trouvé entre ces 2 personnes");
            } else { // tchat was found in other side
                tchatEntity =  tchatRepository.findByUser1AndUser2(message.getRecipient(), message.getSender());
            }
        } else {
            tchatEntity = tchatRepository.findByUser1AndUser2(message.getSender(), message.getRecipient());
        }
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        message.setDateTime(LocalDateTime.now());
        messageRepository.save(message);
        List<String> messages = tchatEntity.getMessages();
        messages.add(message.getId());
        tchatEntity.setMessages(messages);
        tchatRepository.save(tchatEntity);
        return ResponseEntity.status(HttpStatus.OK).body("Message ajouté avec succès");
    }

    @GetMapping("getNotReceivedMessages")
    public List<MessageEntity> getNotReceivedMessages(@RequestParam String loginUser) {
        String idUser = userRepository.findByLoginIgnoreCase(loginUser).getId();
        return messageRepository.findByRecipientAndIsReceived(idUser,false);
    }
}

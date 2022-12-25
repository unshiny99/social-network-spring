package com.bd.socialnetwork.Repository;

import com.bd.socialnetwork.Entity.MessageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<MessageEntity, Long> {
    List<MessageEntity> findByRecipientAndIsReceived(String recipient, boolean isReceived);
}

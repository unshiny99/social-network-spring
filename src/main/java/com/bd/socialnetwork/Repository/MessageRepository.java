package com.bd.socialnetwork.Repository;

import com.bd.socialnetwork.Entity.MessageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<MessageEntity, Long> {

}

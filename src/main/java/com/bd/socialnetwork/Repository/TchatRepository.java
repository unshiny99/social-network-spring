package com.bd.socialnetwork.Repository;

import com.bd.socialnetwork.Entity.TchatEntity;
import com.bd.socialnetwork.Entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TchatRepository extends MongoRepository<TchatEntity, Long> {
    boolean existsUserEntityByUser1(String login);
    boolean existsUserEntityByUser2(String login);
    TchatEntity findByUser1AndUser2(String idUser1 , String idUser2);
}

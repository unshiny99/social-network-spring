package com.bd.socialnetwork;

import com.bd.socialnetwork.Entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserEntity, Long> {
    boolean existsUserEntityByLogin(String login);

    UserEntity findByLogin(String login);
}

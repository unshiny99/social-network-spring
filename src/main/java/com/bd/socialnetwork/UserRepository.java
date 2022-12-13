package com.bd.socialnetwork;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserEntity, Long> {
    boolean existsUserEntityByLogin(String login);

    UserEntity findByLogin(String login);
}

package com.bd.socialnetwork.Repository;

import com.bd.socialnetwork.Entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<UserEntity, Long> {
    boolean existsUserEntityByLoginIgnoreCase(String login);

    UserEntity findByLoginIgnoreCase(String login);
    UserEntity findById(String id);
    List<UserEntity> findByDescriptionLikeIgnoreCase(String description);
}
